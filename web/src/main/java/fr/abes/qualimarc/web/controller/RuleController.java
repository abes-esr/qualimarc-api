package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Slf4j
public class RuleController {
    @Autowired
    private NoticeService service;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private UtilsMapper mapper;

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return service.getBiblioByPpn(ppn);
    }

    @PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultAnalyseResponseDto checkPpn(@Valid @RequestBody PpnWithRuleSetsRequestDto requestBody) {

        Set<RuleSet> ruleSets = new HashSet<>();
        Set<FamilleDocument> familleDocuments =  new HashSet<>();
        Set<TypeThese> typeThese = new HashSet<>();

        if ((requestBody.getFamilleDocumentSet()!= null) && (!requestBody.getFamilleDocumentSet().isEmpty())) {
            for (TypeThese enumTypeThese : EnumUtils.getEnumList(TypeThese.class)) {
                if (requestBody.getFamilleDocumentSet().stream().map(f -> f.getId()).collect(Collectors.toList()).contains(enumTypeThese.name())) {
                    typeThese.add(enumTypeThese);
                    //suppression du type these de la requête pour ne pas altérer la récupération de familles de documents dans la base
                    requestBody.getFamilleDocumentSet().removeIf(f -> f.getId().equals(enumTypeThese.name()));
                }
            }
            familleDocuments = mapper.mapSet(requestBody.getFamilleDocumentSet(),FamilleDocument.class);
        }
        if ((requestBody.getRuleSet() != null) && (!requestBody.getRuleSet().isEmpty())){
            ruleSets = mapper.mapSet(requestBody.getRuleSet(),RuleSet.class);
        }
        return mapper.map(ruleService.checkRulesOnNotices(ruleService.getResultRulesList(requestBody.getTypeAnalyse(), familleDocuments, typeThese, ruleSets), requestBody.getPpnList()), ResultAnalyseResponseDto.class);
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
            if (zonesGeneriques.size() > 0) {
                int i = 0;
                for (String zoneGenerique : zonesGeneriques) {
                    if (rule instanceof PresenceZoneWebDto)
                        rulesEntity.add(mapper.map(new PresenceZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceZoneWebDto) rule).isPresent()), ComplexRule.class));
                    if (rule instanceof PresenceSousZoneWebDto)
                        rulesEntity.add(mapper.map(new PresenceSousZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceSousZoneWebDto) rule).getSousZone(), ((PresenceSousZoneWebDto) rule).isPresent()), ComplexRule.class));
                    if (rule instanceof PresenceSousZonesMemeZoneWebDto)
                        rulesEntity.add(mapper.map(new PresenceSousZonesMemeZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceSousZonesMemeZoneWebDto) rule).getSousZones()), ComplexRule.class));
                    if (rule instanceof PositionSousZoneWebDto)
                        rulesEntity.add(mapper.map(new PositionSousZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PositionSousZoneWebDto) rule).getSousZone(), ((PositionSousZoneWebDto) rule).getPosition()), ComplexRule.class));
                    if (rule instanceof NombreZoneWebDto)
                        rulesEntity.add(mapper.map(new NombreZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreZoneWebDto) rule).getOperateur(), ((NombreZoneWebDto) rule).getOccurrences()), ComplexRule.class));
                    if (rule instanceof NombreSousZoneWebDto)
                        rulesEntity.add(mapper.map(new NombreSousZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreSousZoneWebDto) rule).getSousZone(), ((NombreSousZoneWebDto) rule).getZoneCible(), ((NombreSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                    if (rule instanceof TypeCaractereWebDto)
                        rulesEntity.add(mapper.map(new TypeCaractereWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((TypeCaractereWebDto) rule).getSousZone(), ((TypeCaractereWebDto) rule).getTypeCaracteres()), ComplexRule.class));
                    if (rule instanceof PresenceChaineCaracteresWebDto)
                        rulesEntity.add(mapper.map(new PresenceChaineCaracteresWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceChaineCaracteresWebDto) rule).getSousZone(), ((PresenceChaineCaracteresWebDto) rule).getTypeDeVerification(), ((PresenceChaineCaracteresWebDto) rule).getListChaineCaracteres()), ComplexRule.class));
                    if (rule instanceof NombreCaracteresWebDto)
                        rulesEntity.add(mapper.map(new NombreCaracteresWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, ((NombreCaracteresWebDto) rule).getSousZone(), rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreCaracteresWebDto) rule).getOperateur(), ((NombreCaracteresWebDto) rule).getOccurrences()), ComplexRule.class));
                    if (rule instanceof IndicateurWebDto)
                        rulesEntity.add(mapper.map(new IndicateurWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((IndicateurWebDto) rule).getIndicateur(), ((IndicateurWebDto) rule).getValeur()), ComplexRule.class));
                    if (rule instanceof ComparaisonContenuSousZoneWebDto)
                        if (((ComparaisonContenuSousZoneWebDto) rule).getNombreCaracteres() != null) {
                            rulesEntity.add(mapper.map(new ComparaisonContenuSousZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZone(), ((ComparaisonContenuSousZoneWebDto) rule).getTypeVerification(), ((ComparaisonContenuSousZoneWebDto) rule).getNombreCaracteres(), ((ComparaisonContenuSousZoneWebDto) rule).getZoneCible(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                        } else {
                            rulesEntity.add(mapper.map(new ComparaisonContenuSousZoneWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZone(), ((ComparaisonContenuSousZoneWebDto) rule).getTypeVerification(), ((ComparaisonContenuSousZoneWebDto) rule).getZoneCible(), ((ComparaisonContenuSousZoneWebDto) rule).getSousZoneCible()), ComplexRule.class));
                        }
                    i++;
                }
            } else {
                rulesEntity.add(mapper.map(rule, ComplexRule.class));
            }
        }
        return rulesEntity;
    }

    private Integer generateNewId(Integer id, int i) {
        return id + 50000 + i;
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
}