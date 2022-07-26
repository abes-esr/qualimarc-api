package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.web.dto.ControllingPpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.web.dto.ControllingPpnWithRuleSetsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class PublicController {
    @Autowired
    private NoticeBibioService service;

    @Autowired
    private RuleService ruleService;

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return service.getByPpn(ppn);
    }

    @PostMapping("/check/")
    public List<ResultRules> checkPpn(@RequestBody ControllingPpnWithRuleSetsRequestDto requestBody) {
        return ruleService.getResultRulesList(requestBody.getPpnList(), requestBody.getRulesSetList());
    }
}


