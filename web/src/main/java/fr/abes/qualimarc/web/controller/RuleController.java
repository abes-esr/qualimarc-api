package fr.abes.qualimarc.web.controller;

import com.google.common.collect.Lists;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.service.JournalService;
import fr.abes.qualimarc.core.service.NoticeService;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.TypeThese;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.RuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.*;
import fr.abes.qualimarc.web.dto.indexrules.contenu.*;
import fr.abes.qualimarc.web.dto.indexrules.dependance.ReciprociteWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Slf4j
public class RuleController {
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private UtilsMapper mapper;

    @Autowired
    @Qualifier("asyncExecutor")
    private Executor asyncExecutor;

    @Value("${spring.task.execution.pool.core-size}")
    private Integer nbThread;

    private int nbTotalPpn;

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return noticeService.getBiblioByPpn(ppn);
    }

    @PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultAnalyseResponseDto checkPpn(@Valid @RequestBody PpnWithRuleSetsRequestDto requestBody) {
        Long start = System.currentTimeMillis();
        ruleService.resetCn();
        //initialisation de l'entrée dans la table journée
        JournalAnalyse journal = new JournalAnalyse(new Date(), requestBody.getTypeAnalyse(), requestBody.isReplayed());
        Set<RuleSet> ruleSets = new HashSet<>();
        Set<FamilleDocument> familleDocuments =  new HashSet<>();
        Set<TypeThese> typeThese = new HashSet<>();

        if ((requestBody.getFamilleDocumentSet()!= null) && (!requestBody.getFamilleDocumentSet().isEmpty())) {
            journal.setTypeDocument(requestBody.getFamilleDocumentSet().stream().map(FamilleDocumentWebDto::getId).collect(Collectors.joining("|")));
            for (TypeThese enumTypeThese : EnumUtils.getEnumList(TypeThese.class)) {
                if (requestBody.getFamilleDocumentSet().stream().map(FamilleDocumentWebDto::getId).collect(Collectors.toList()).contains(enumTypeThese.name())) {
                    typeThese.add(enumTypeThese);
                    //suppression du type these de la requête pour ne pas altérer la récupération de familles de documents dans la base
                    requestBody.getFamilleDocumentSet().removeIf(f -> f.getId().equals(enumTypeThese.name()));
                }
            }
            familleDocuments = mapper.mapSet(requestBody.getFamilleDocumentSet(),FamilleDocument.class);
        }
        if ((requestBody.getRuleSet() != null) && (!requestBody.getRuleSet().isEmpty())){
            ruleSets = mapper.mapSet(requestBody.getRuleSet(),RuleSet.class);
            journal.setRuleSet(ruleSets.stream().map(ruleSet -> ruleSet.getId().toString()).collect(Collectors.joining("|")));
        }
        this.nbTotalPpn = requestBody.getPpnList().size();
        List<List<String>> splittedList = Lists.partition(requestBody.getPpnList(), requestBody.getPpnList().size() / nbThread + 1);
        List<CompletableFuture<ResultAnalyse>> resultList = new ArrayList<>();

        ResultAnalyse resultAnalyse;
        if(splittedList.size() > 1) {
            for (List<String> ppnList : splittedList)
                resultList.add(ruleService.checkRulesOnNotices(ruleService.getResultRulesList(requestBody.getTypeAnalyse(), familleDocuments, typeThese, ruleSets), ppnList));

            //biFunction permet de prendre le résultat de 2 traitements en parallèle et de les fusionner en un troisième qui est retourné
            BiFunction<ResultAnalyse, ResultAnalyse, ResultAnalyse> biFunction = (res1, res2) -> {
                res1.merge(res2);
                return res1;
            };

            //on récupère chaque traitement lancé en parallèle et on le combine au précédent en fusionnant les résultats
            resultAnalyse = resultList.stream().reduce((res1, res2) -> res1.thenCombineAsync(res2, biFunction, asyncExecutor)).orElse(CompletableFuture.completedFuture(new ResultAnalyse())).join();
        } else {
            resultAnalyse = ruleService.checkRulesOnNotices(ruleService.getResultRulesList(requestBody.getTypeAnalyse(), familleDocuments, typeThese, ruleSets), requestBody.getPpnList()).join();
        }

        journal.setNbPpnAnalyse(resultAnalyse.getPpnAnalyses().size());
        journal.setNbPpnOk(resultAnalyse.getPpnOk().size());
        journal.setNbPpnErreur(resultAnalyse.getPpnErrones().size());
        journal.setNbPpnInconnus(resultAnalyse.getPpnInconnus().size());
        journalService.addAnalyseIntoJournal(journal);
        journalService.saveStatsMessages(resultAnalyse.getStatsMessagesList());

        ResultAnalyseResponseDto responseDto = mapper.map(resultAnalyse, ResultAnalyseResponseDto.class);
        long end = System.currentTimeMillis();
        log.debug("Temps de traitement : " + (end - start));
        return responseDto;
    }


    @PostMapping(value = "/indexRules", consumes = {"text/yaml", "text/yml"})
    public void indexRules(@Valid @RequestBody ListRulesWebDto rules) {
        List<ComplexRule> rulesEntity = handleRulesWebDto(rules);
        try {
            ruleService.saveAll(rulesEntity);
        } catch (DataIntegrityViolationException ex) {
            //exception levée dans le cas ou un id est déjà pris
            throw new IllegalArgumentException("Une règle avec l'identifiant " + StringUtils.substringBetween(ex.getMessage(), "#", "]") + " existe déjà");
        }
    }

    private List<ComplexRule> handleRulesWebDto(ListRulesWebDto rules) {
        List<ComplexRule> rulesEntity = new ArrayList<>();
        for (SimpleRuleWebDto rule : rules.getRules()) {
            if (rule instanceof DependencyWebDto)
                throw new IllegalArgumentException("Une règle simple ne peut pas être une règle de dépendance");
            if (rule instanceof ReciprociteWebDto)
                throw new IllegalArgumentException("Une règle simple ne peut pas être de type reciprocite");
            List<String> zonesGeneriques = referenceService.getZonesGeneriques(rule.getZone());
//            List<String> zonesGeneriquesCible = (rule instanceof ComparaisonDateWebDto) ? referenceService.getZonesGeneriques(((ComparaisonDateWebDto) rule).getZoneCible()) : null;
            if (zonesGeneriques.size() > 0) {
                int indexForGeneratingId = 0;
                for (String zoneGenerique : zonesGeneriques) {
                    if (rule instanceof PresenceZoneWebDto)
                        rulesEntity.add(mapper.map(new PresenceZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceZoneWebDto) rule).isPresent()), ComplexRule.class));
                    if (rule instanceof PresenceSousZoneWebDto)
                        rulesEntity.add(mapper.map(new PresenceSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceSousZoneWebDto) rule).getSousZone(), ((PresenceSousZoneWebDto) rule).isPresent()), ComplexRule.class));
                    if (rule instanceof PresenceSousZonesMemeZoneWebDto)
                        rulesEntity.add(mapper.map(new PresenceSousZonesMemeZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceSousZonesMemeZoneWebDto) rule).getSousZones()), ComplexRule.class));
                    if (rule instanceof PositionSousZoneWebDto)
                        rulesEntity.add(mapper.map(new PositionSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PositionSousZoneWebDto) rule).getSousZone(), ((PositionSousZoneWebDto) rule).getPosition()), ComplexRule.class));
                    if (rule instanceof NombreZoneWebDto)
                        rulesEntity.add(mapper.map(new NombreZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreZoneWebDto) rule).getComparaisonOperateur(), ((NombreZoneWebDto) rule).getOccurrences()), ComplexRule.class));
                    if (rule instanceof NombreSousZoneWebDto)
                        rulesEntity.add(mapper.map(new NombreSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreSousZoneWebDto) rule).getSousZone(), ((NombreSousZoneWebDto) rule).getZoneCible(), ((NombreSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                    if (rule instanceof TypeCaractereWebDto)
                        rulesEntity.add(mapper.map(new TypeCaractereWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((TypeCaractereWebDto) rule).getSousZone(), ((TypeCaractereWebDto) rule).getTypeCaracteres()), ComplexRule.class));
                    if (rule instanceof PresenceChaineCaracteresWebDto)
                        rulesEntity.add(mapper.map(new PresenceChaineCaracteresWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceChaineCaracteresWebDto) rule).getSousZone(), ((PresenceChaineCaracteresWebDto) rule).getTypeDeVerification(), ((PresenceChaineCaracteresWebDto) rule).getListChaineCaracteres()), ComplexRule.class));
                    if (rule instanceof NombreCaracteresWebDto)
                        rulesEntity.add(mapper.map(new NombreCaracteresWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, ((NombreCaracteresWebDto) rule).getSousZone(), rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreCaracteresWebDto) rule).getComparaisonOperateur(), ((NombreCaracteresWebDto) rule).getOccurrences()), ComplexRule.class));
                    if (rule instanceof IndicateurWebDto)
                        rulesEntity.add(mapper.map(new IndicateurWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((IndicateurWebDto) rule).getIndicateur(), ((IndicateurWebDto) rule).getValeur()), ComplexRule.class));
                    if (rule instanceof ComparaisonDateWebDto)
                        rulesEntity.add(mapper.map(new ComparaisonDateWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonDateWebDto) rule).getSousZone(), ((ComparaisonDateWebDto) rule).getPositionStart(), ((ComparaisonDateWebDto) rule).getPositionEnd(), ((ComparaisonDateWebDto) rule).getZoneCible(), ((ComparaisonDateWebDto) rule).getSousZoneCible(), ((ComparaisonDateWebDto) rule).getPositionStartCible(), ((ComparaisonDateWebDto) rule).getPositionEndCible(),((ComparaisonDateWebDto) rule).getComparateur()), ComplexRule.class));
                    if (rule instanceof ComparaisonContenuSousZoneWebDto) {
                        if (((ComparaisonContenuSousZoneWebDto) rule).getNombreCaracteres() != null) {
                            rulesEntity.add(mapper.map(new ComparaisonContenuSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZone(), ((ComparaisonContenuSousZoneWebDto) rule).getTypeVerification(), ((ComparaisonContenuSousZoneWebDto) rule).getNombreCaracteres(), ((ComparaisonContenuSousZoneWebDto) rule).getZoneCible(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                        } else {
                            rulesEntity.add(mapper.map(new ComparaisonContenuSousZoneWebDto(generateNewId(rule.getId(), indexForGeneratingId), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZone(), ((ComparaisonContenuSousZoneWebDto) rule).getTypeVerification(), ((ComparaisonContenuSousZoneWebDto) rule).getZoneCible(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
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

    @PostMapping(value = "/indexComplexRules", consumes = {"text/yaml", "text/yml"})
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

    /**
     * Methode pour la bar de progress
     * @return
     */
    @GetMapping("/getStatus")
    public String getStatus() {
        return String.format("%.0f%%", ruleService.getCn(this.nbTotalPpn));
    }
}