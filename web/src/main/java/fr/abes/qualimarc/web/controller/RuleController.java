package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ListComplexRulesWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ListRulesWebDto;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private UtilsMapper mapper;

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return service.getByPpn(ppn);
    }

    @PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultAnalyseResponseDto checkPpn(@Valid @RequestBody PpnWithRuleSetsRequestDto requestBody) {

        Set<RuleSet> ruleSets = new HashSet<>();
        Set<FamilleDocument> familleDocuments =  new HashSet<>();

        if ((requestBody.getFamilleDocumentSet()!= null) && (!requestBody.getFamilleDocumentSet().isEmpty())) {
            familleDocuments = mapper.mapSet(requestBody.getFamilleDocumentSet(),FamilleDocument.class);
        }
        if ((requestBody.getRuleSet() != null) && (!requestBody.getRuleSet().isEmpty())){
            ruleSets = mapper.mapSet(requestBody.getRuleSet(),RuleSet.class);
        }
        return mapper.map(ruleService.checkRulesOnNotices(requestBody.getPpnList(), ruleService.getResultRulesList(requestBody.getTypeAnalyse(), familleDocuments, ruleSets)), ResultAnalyseResponseDto.class);
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
            rulesEntity.add(mapper.map(rule, ComplexRule.class));
        }
        return rulesEntity;
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