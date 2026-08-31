package fr.abes.qualimarc.web.controller;

import com.google.common.collect.Lists;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalAnalyse;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalFamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalRuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.service.JournalService;
import fr.abes.qualimarc.core.service.NoticeService;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.TypeThese;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.AnalysisLaunchResponseDto;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.RuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.DependencyWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ListComplexRulesWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ListRulesWebDto;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.ComparaisonContenuSousZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.ComparaisonDateWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.IndicateurWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.NombreCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.PresenceChaineCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.TypeCaractereWebDto;
import fr.abes.qualimarc.web.dto.indexrules.dependance.ReciprociteWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.NombreSousZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.NombreZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.PositionSousZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.PresenceSousZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.PresenceSousZonesMemeZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.PresenceZoneWebDto;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Slf4j
public class RuleController {
    private final NoticeService noticeService;

    private final RuleService ruleService;

    private final ReferenceService referenceService;

    private final JournalService journalService;

    private final UtilsMapper mapper;

    private final Executor asyncExecutor;

    @Value("${spring.task.execution.pool.core-size}")
    private Integer nbThread;

    // FIX incident TEST du 31/08/2026 : duree maximale d'une analyse. Au-dela, le future
    // est declare en echec (TimeoutException) pour qu'une analyse bloquee ne reste plus
    // jamais pendante indefiniment.
    @Value("${qualimarc.analysis.timeout-minutes:30}")
    private long analysisTimeoutMinutes;

    // FIX incident TEST du 31/08/2026 : nombre maximal d'analyses simultanees. Le pool
    // asynchrone est limite (8 threads) et chaque analyse en mobilise plusieurs ; au-dela
    // de cette limite les analyses s'affameraient entre elles jusqu'a la panne.
    @Value("${qualimarc.analysis.max-concurrent:2}")
    private int maxConcurrentAnalyses;

    private final Map<Integer,Integer> mapIdToNbTotalPpn = new ConcurrentHashMap<>();

    private final Map<Integer, CompletableFuture<ResultAnalyseResponseDto>> analysisResultsById = new ConcurrentHashMap<>();

    // FIX incident TEST du 31/08/2026 : date de lancement de chaque analyse, utilisee par
    // la purge periodique pour detecter les analyses qui ne se terminent jamais.
    private final Map<Integer, Long> analysisStartTimesById = new ConcurrentHashMap<>();

    public RuleController(NoticeService noticeService, RuleService ruleService, ReferenceService referenceService, JournalService journalService, UtilsMapper mapper, @Qualifier("asyncExecutor") Executor asyncExecutor) {
        this.noticeService = noticeService;
        this.ruleService = ruleService;
        this.referenceService = referenceService;
        this.journalService = journalService;
        this.mapper = mapper;
        this.asyncExecutor = asyncExecutor;
    }

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return noticeService.getBiblioByPpn(ppn);
    }

    @PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalysisLaunchResponseDto> checkPpn(@Valid @RequestBody PpnWithRuleSetsRequestDto requestBody) {
        int analysisId = requestBody.getId();

        // FIX incident TEST du 31/08/2026 : garde-fou contre la saturation du pool.
        // Tant que le nombre maximal d'analyses en cours est atteint, on refuse la
        // nouvelle analyse avec un HTTP 503 explicite (le client peut reessayer) au
        // lieu de l'empiler dans la file jusqu'au rejet global du service.
        long runningAnalyses = analysisResultsById.values().stream().filter(future -> !future.isDone()).count();
        if (runningAnalyses >= maxConcurrentAnalyses) {
            log.warn("Analyse {} refusee : {} analyses deja en cours (maximum {})", analysisId, runningAnalyses, maxConcurrentAnalyses);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        cleanupAnalysis(analysisId);
        ruleService.resetCn(analysisId);
        mapIdToNbTotalPpn.put(analysisId, requestBody.getPpnList().size());

        long start = System.currentTimeMillis();
        CompletableFuture<ResultAnalyseResponseDto> analysisFuture;
        try {
            analysisFuture = CompletableFuture.supplyAsync(
                    () -> executeAnalysis(requestBody, start),
                    asyncExecutor
            );
        } catch (TaskRejectedException ex) {
            // FIX incident TEST du 31/08/2026 : quand le pool et sa file d'attente sont
            // satures, la tache est rejetee. Avant ce correctif, l'exception remontait en
            // HTTP 500 opaque ; on renvoie desormais un 503 clair et on nettoie l'etat.
            log.warn("Analyse {} rejetee : le pool d'analyse est sature", analysisId, ex);
            cleanupAnalysis(analysisId);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        // FIX incident TEST du 31/08/2026 : une analyse ne doit plus jamais durer
        // indefiniment (barre de progression figee a zero pour l'utilisateur).
        analysisFuture.orTimeout(analysisTimeoutMinutes, TimeUnit.MINUTES);
        analysisFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Erreur inattendue pendant l'analyse {}", analysisId, throwable);
            }
        });
        analysisResultsById.put(analysisId, analysisFuture);
        analysisStartTimesById.put(analysisId, start);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new AnalysisLaunchResponseDto(analysisId));
    }

    @GetMapping("/result/{id}")
    public ResponseEntity<ResultAnalyseResponseDto> getResult(@PathVariable int id) {
        CompletableFuture<ResultAnalyseResponseDto> analysisFuture = analysisResultsById.get(id);
        if (analysisFuture == null) {
            throw new IllegalArgumentException("L'identifiant pour obtenir le resultat de l'analyse est incorrect, Actualiser votre page");
        }

        if (!analysisFuture.isDone()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        try {
            ResultAnalyseResponseDto result = analysisFuture.join();
            cleanupAnalysis(id);
            return ResponseEntity.ok(result);
        } catch (CompletionException ex) {
            cleanupAnalysis(id);
            throw new IllegalStateException("Une erreur inattendue est survenue pendant l'analyse", ex.getCause());
        }
    }

    private ResultAnalyseResponseDto executeAnalysis(PpnWithRuleSetsRequestDto requestBody, long start) {
        Date dateJour = Calendar.getInstance().getTime();
        JournalAnalyse journalAnalyse = new JournalAnalyse(dateJour, requestBody.getTypeAnalyse(), requestBody.isReplayed());
        Set<RuleSet> ruleSets = new HashSet<>();
        Set<FamilleDocument> familleDocuments =  new HashSet<>();
        Set<TypeThese> typeThese = new HashSet<>();

        if ((requestBody.getFamilleDocumentSet()!= null) && (!requestBody.getFamilleDocumentSet().isEmpty())) {
            if (!requestBody.isReplayed()) {
                requestBody.getFamilleDocumentSet().stream().map(FamilleDocumentWebDto::getId).forEach(id -> {
                    JournalFamilleDocument journalFamilleDocument = new JournalFamilleDocument(dateJour, id);
                    journalService.saveJournalFamille(journalFamilleDocument);
                });
            }
            for (TypeThese enumTypeThese : EnumUtils.getEnumList(TypeThese.class)) {
                if (requestBody.getFamilleDocumentSet().stream().map(FamilleDocumentWebDto::getId).collect(Collectors.toList()).contains(enumTypeThese.name())) {
                    typeThese.add(enumTypeThese);
                    requestBody.getFamilleDocumentSet().removeIf(f -> f.getId().equals(enumTypeThese.name()));
                }
            }
            familleDocuments = mapper.mapSet(requestBody.getFamilleDocumentSet(),FamilleDocument.class);
        }
        if ((requestBody.getRuleSet() != null) && (!requestBody.getRuleSet().isEmpty())){
            ruleSets = mapper.mapSet(requestBody.getRuleSet(),RuleSet.class);
            if (!requestBody.isReplayed()) {
                ruleSets.stream().map(RuleSet::getId).forEach(id -> {
                    JournalRuleSet journalRuleSet = new JournalRuleSet(dateJour, id);
                    journalService.saveJournalRuleSet(journalRuleSet);
                });
            }
        }
        List<List<String>> splittedList = Lists.partition(requestBody.getPpnList(), requestBody.getPpnList().size() / nbThread + 1);
        List<CompletableFuture<ResultAnalyse>> resultList = new ArrayList<>();
        Set<ComplexRule> rulesToApply = ruleService.getResultRulesList(
                requestBody.getTypeAnalyse(),
                familleDocuments,
                typeThese,
                ruleSets
        );

        ResultAnalyse resultAnalyse;
        if(splittedList.size() > 1) {
            for (List<String> ppnList : splittedList) {
                resultList.add(ruleService.checkRulesOnNotices(requestBody.getId(), rulesToApply, ppnList, requestBody.isReplayed()));
            }

            // FIX incident TEST du 31/08/2026 : la fusion des resultats passait par
            // thenCombineAsync soumis au meme pool que les analyses elles-memes. Quand
            // plusieurs analyses tournaient en parallele, ces taches de fusion entraient en
            // concurrence avec les partitions d'analyse et pouvaient affamer le pool.
            // La fusion se fait desormais de maniere synchrone, une fois toutes les
            // partitions terminees : plus aucune tache supplementaire n'est soumise au pool.
            CompletableFuture.allOf(resultList.toArray(new CompletableFuture[0])).join();
            resultAnalyse = resultList.get(0).join();
            for (int i = 1; i < resultList.size(); i++) {
                resultAnalyse.merge(resultList.get(i).join());
            }
        } else {
            resultAnalyse = ruleService.checkRulesOnNotices(requestBody.getId(), rulesToApply, requestBody.getPpnList(), requestBody.isReplayed()).join();
        }

        journalAnalyse.setNbPpnAnalyse(resultAnalyse.getPpnAnalyses().size());
        journalAnalyse.setNbPpnOk(resultAnalyse.getPpnOk().size());
        journalAnalyse.setNbPpnErreur(resultAnalyse.getPpnErrones().size());
        journalAnalyse.setNbPpnInconnus(resultAnalyse.getPpnInconnus().size());
        journalService.saveJournalAnalyse(journalAnalyse);
        journalService.saveJournalMessages(resultAnalyse.getJournalMessagesList());

        ResultAnalyseResponseDto responseDto = mapper.map(resultAnalyse, ResultAnalyseResponseDto.class);
        long end = System.currentTimeMillis();
        log.debug("Temps de traitement : {}", end - start);
        return responseDto;
    }

    private void cleanupAnalysis(Integer id) {
        analysisResultsById.remove(id);
        mapIdToNbTotalPpn.remove(id);
        analysisStartTimesById.remove(id);
        ruleService.clearCn(id);
    }

    /**
     * FIX incident TEST du 31/08/2026 : purge periodique des analyses orphelines.
     * Avant ce correctif, une entree d'analysisResultsById n'etait retiree que si le
     * client revenait chercher son resultat : si l'utilisateur fermait son onglet, le
     * future (et son contexte) restait en memoire indefiniment. Toutes les 5 minutes on
     * retire donc les analyses terminees, ainsi que celles qui ont depasse le double du
     * delai d'analyse (elles sont considerees perdues).
     */
    @Scheduled(fixedDelayString = "PT5M")
    public void purgeFinishedAnalyses() {
        long maxAgeMillis = TimeUnit.MINUTES.toMillis(analysisTimeoutMinutes) * 2;
        long now = System.currentTimeMillis();
        for (Map.Entry<Integer, CompletableFuture<ResultAnalyseResponseDto>> entry : analysisResultsById.entrySet()) {
            boolean finished = entry.getValue().isDone();
            boolean lost = now - analysisStartTimesById.getOrDefault(entry.getKey(), now) > maxAgeMillis;
            if (finished || lost) {
                if (lost && !finished) {
                    log.warn("Purge de l'analyse {} qui ne s'est jamais terminee", entry.getKey());
                }
                cleanupAnalysis(entry.getKey());
            }
        }
    }

    @PostMapping(value = "/indexRules", consumes = {"application/x-yaml", "application/yaml", "text/yaml", "text/yml"})
    public void indexRules(@Valid @RequestBody ListRulesWebDto rules) {
        List<ComplexRule> rulesEntity = handleRulesWebDto(rules);
        try {
            ruleService.saveAll(rulesEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Une règle avec l'identifiant " + StringUtils.substringBetween(ex.getMessage(), "#", "]") + " existe déjà");
        }
    }

    private List<ComplexRule> handleRulesWebDto(ListRulesWebDto rules) {
        List<ComplexRule> rulesEntity = new ArrayList<>();
        for (SimpleRuleWebDto rule : rules.getRules()) {
            if (rule instanceof DependencyWebDto) {
                throw new IllegalArgumentException("Une règle simple ne peut pas être une règle de dépendance");
            }
            if (rule instanceof ReciprociteWebDto) {
                throw new IllegalArgumentException("Une règle simple ne peut pas être de type reciprocite");
            }
            List<String> zonesGeneriques = referenceService.getZonesGeneriques(rule.getZone());
            if (!zonesGeneriques.isEmpty()) {
                int indexForGeneratingId = 0;
                for (String zoneGenerique : zonesGeneriques) {
                    if (rule instanceof PresenceZoneWebDto) {
                        rulesEntity.add(mapper.map(new PresenceZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceZoneWebDto) rule).isPresent()), ComplexRule.class));
                    }
                    if (rule instanceof PresenceSousZoneWebDto) {
                        rulesEntity.add(mapper.map(new PresenceSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceSousZoneWebDto) rule).getSousZone(), ((PresenceSousZoneWebDto) rule).isPresent()), ComplexRule.class));
                    }
                    if (rule instanceof PresenceSousZonesMemeZoneWebDto) {
                        rulesEntity.add(mapper.map(new PresenceSousZonesMemeZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceSousZonesMemeZoneWebDto) rule).getSousZones()), ComplexRule.class));
                    }
                    if (rule instanceof PositionSousZoneWebDto) {
                        rulesEntity.add(mapper.map(new PositionSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PositionSousZoneWebDto) rule).getSousZone(), ((PositionSousZoneWebDto) rule).getPositions(), ((PositionSousZoneWebDto) rule).getBooleanOperateur()), ComplexRule.class));
                    }
                    if (rule instanceof NombreZoneWebDto) {
                        rulesEntity.add(mapper.map(new NombreZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreZoneWebDto) rule).getComparaisonOperateur(), ((NombreZoneWebDto) rule).getOccurrences()), ComplexRule.class));
                    }
                    if (rule instanceof NombreSousZoneWebDto) {
                        rulesEntity.add(mapper.map(new NombreSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreSousZoneWebDto) rule).getSousZone(), ((NombreSousZoneWebDto) rule).getZoneCible(), ((NombreSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                    }
                    if (rule instanceof TypeCaractereWebDto) {
                        rulesEntity.add(mapper.map(new TypeCaractereWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((TypeCaractereWebDto) rule).getSousZone(), ((TypeCaractereWebDto) rule).getTypeCaracteres()), ComplexRule.class));
                    }
                    if (rule instanceof PresenceChaineCaracteresWebDto) {
                        rulesEntity.add(mapper.map(new PresenceChaineCaracteresWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceChaineCaracteresWebDto) rule).getSousZone(), ((PresenceChaineCaracteresWebDto) rule).getPositionStart(), ((PresenceChaineCaracteresWebDto) rule).getPositionEnd(), ((PresenceChaineCaracteresWebDto) rule).getTypeDeVerification(), ((PresenceChaineCaracteresWebDto) rule).getListChaineCaracteres()), ComplexRule.class));
                    }
                    if (rule instanceof NombreCaracteresWebDto) {
                        rulesEntity.add(mapper.map(new NombreCaracteresWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, ((NombreCaracteresWebDto) rule).getSousZone(), rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreCaracteresWebDto) rule).getComparaisonOperateur(), ((NombreCaracteresWebDto) rule).getOccurrences()), ComplexRule.class));
                    }
                    if (rule instanceof IndicateurWebDto) {
                        rulesEntity.add(mapper.map(new IndicateurWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((IndicateurWebDto) rule).getIndicateur(), ((IndicateurWebDto) rule).getValeur(), ((IndicateurWebDto) rule).getTypeDeVerification()), ComplexRule.class));
                    }
                    if (rule instanceof ComparaisonDateWebDto) {
                        rulesEntity.add(mapper.map(new ComparaisonDateWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonDateWebDto) rule).getSousZone(), ((ComparaisonDateWebDto) rule).getPositionStart(), ((ComparaisonDateWebDto) rule).getPositionEnd(), ((ComparaisonDateWebDto) rule).getZoneCible(), ((ComparaisonDateWebDto) rule).getSousZoneCible(), ((ComparaisonDateWebDto) rule).getPositionStartCible(), ((ComparaisonDateWebDto) rule).getPositionEndCible(),((ComparaisonDateWebDto) rule).getComparateur()), ComplexRule.class));
                    }
                    if (rule instanceof ComparaisonContenuSousZoneWebDto) {
                        if (((ComparaisonContenuSousZoneWebDto) rule).getNombreCaracteres() != null) {
                            rulesEntity.add(mapper.map(new ComparaisonContenuSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZone(), ((ComparaisonContenuSousZoneWebDto) rule).getTypeVerification(), ((ComparaisonContenuSousZoneWebDto) rule).getNombreCaracteres(), ((ComparaisonContenuSousZoneWebDto) rule).getZoneCible(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                        } else {
                            rulesEntity.add(mapper.map(new ComparaisonContenuSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), rule.isAffichageEtiquette(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZone(), ((ComparaisonContenuSousZoneWebDto) rule).getTypeVerification(), ((ComparaisonContenuSousZoneWebDto) rule).getZoneCible(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                        }
                    }
                    indexForGeneratingId++;
                }
            } else {
                rulesEntity.add(mapper.map(rule, ComplexRule.class));
            }
        }
        return rulesEntity;
    }

    private Integer generateNewId(Integer id, int i) {
        return id * 100 + i;
    }

    @PostMapping(value = "/indexComplexRules", consumes = {"application/x-yaml", "application/yaml", "text/yaml", "text/yml"})
    public void indexComplexRules(@Valid @RequestBody ListComplexRulesWebDto rules) {
        List<ComplexRule> rulesEntity = handleComplexRulesWebDto(rules);
        try {
            ruleService.saveAll(rulesEntity);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new IllegalArgumentException("Une règle simple avec l'identifiant " + StringUtils.substringBetween(ex.getCause().getCause().getMessage(), ")=(", ")") + " existe déjà");
            }
            throw new IllegalArgumentException("Une règle complexe avec l'identifiant " + StringUtils.substringBetween(ex.getMessage(), "#", "]") + " existe déjà");
        }
    }

    private List<ComplexRule> handleComplexRulesWebDto(ListComplexRulesWebDto rules) {
        List<ComplexRule> rulesEntity = new ArrayList<>();
        for (ComplexRuleWebDto complexRuleWebDto : rules.getComplexRules()) {
            if(complexRuleWebDto.getRegles().size() > 1) {
                ComplexRule complexRule = mapper.map(complexRuleWebDto, ComplexRule.class);
                rulesEntity.add(complexRule);
            } else {
                throw new IllegalArgumentException("Une règle complexe doit contenir au moins deux règles simples");
            }
        }
        return rulesEntity;
    }

    @GetMapping(value = "/emptyRules")
    public void viderRegles() {
        ruleService.viderRegles();
    }

    @GetMapping(value = "/rules")
    public List<RuleWebDto> getRules() {
        return mapper.mapList(ruleService.getAllComplexRules(), RuleWebDto.class);
    }

    @GetMapping("/getStatus/{id}")
    public String getStatus(@PathVariable int id) {
        CompletableFuture<ResultAnalyseResponseDto> analysisFuture = analysisResultsById.get(id);
        if (analysisFuture != null && analysisFuture.isDone()) {
            return "100%";
        }

        if(ruleService.isCnPresent(id) && mapIdToNbTotalPpn.containsKey(id)) {
            return String.format("%.0f%%", ruleService.getCn(id, mapIdToNbTotalPpn.get(id)));
        }

        throw new IllegalArgumentException("L'identifiant pour obtenir le status de progression est incorrecte, Actualiser votre page");
    }
}
