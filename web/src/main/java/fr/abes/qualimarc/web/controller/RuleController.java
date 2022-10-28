package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ListComplexRulesWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ListRulesWebDto;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.IndicateurWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.NombreCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.PresenceChaineCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.TypeCaractereWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Slf4j
public class RuleController {
    @Autowired
    private NoticeBibioService service;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private UtilsMapper mapper;

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return service.getByPpn(ppn);
    }

    @PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultAnalyseResponseDto checkPpn(@Valid @RequestBody PpnWithRuleSetsRequestDto requestBody) {
        return mapper.map(ruleService.checkRulesOnNotices(requestBody.getPpnList(), ruleService.getResultRulesList(requestBody.getTypeAnalyse(), requestBody.getFamilleDocumentSet(), requestBody.getRuleSet())), ResultAnalyseResponseDto.class);
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
                        rulesEntity.add(mapper.map(new PresenceChaineCaracteresWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((PresenceChaineCaracteresWebDto) rule).getSousZone(), ((PresenceChaineCaracteresWebDto) rule).getTypeDeVerification()), ComplexRule.class));
                    if (rule instanceof NombreCaracteresWebDto)
                        rulesEntity.add(mapper.map(new NombreCaracteresWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, ((NombreCaracteresWebDto) rule).getSousZone(), rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((NombreCaracteresWebDto) rule).getOperateur(), ((NombreCaracteresWebDto) rule).getOccurrences()), ComplexRule.class));
                    if (rule instanceof IndicateurWebDto)
                        rulesEntity.add(mapper.map(new IndicateurWebDto(generateNewId(rule.getId(), i), rule.getIdExcel(), rule.getRuleSetList(), rule.getMessage(), zoneGenerique, rule.getPriority(), rule.getTypesDoc(), rule.getTypesThese(), ((IndicateurWebDto) rule).getIndicateur(), ((IndicateurWebDto) rule).getValeur()), ComplexRule.class));
                    i++;
                }
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
                rulesEntity.add(mapper.map(complexRuleWebDto, ComplexRule.class));
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
}