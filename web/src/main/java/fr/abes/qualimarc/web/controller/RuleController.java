package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.ListRulesWebDto;
import fr.abes.qualimarc.web.dto.indexrules.PresenceZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.RulesWebDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
        List<Rule> rulesEntity = handleRulesWebDto(rules);
        ruleService.saveAll(rulesEntity);
    }

    private List<Rule> handleRulesWebDto(ListRulesWebDto rules) {
        List<RulesWebDto> rulesWebDtos = rules.getListRulesWebDto();
        List<Rule> rulesEntity = new ArrayList<>();
        Iterator<RulesWebDto> rulesIt = rulesWebDtos.iterator();
        while (rulesIt.hasNext()) {
            RulesWebDto rule = rulesIt.next();
            if (rule instanceof PresenceZoneWebDto) {
                rulesEntity.add(mapper.map(rule, PresenceZone.class));
            }
        }
        return rulesEntity;
    }
}