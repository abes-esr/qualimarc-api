package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class PublicController {
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

    @PostMapping("/check/")
    public ResultAnalyseResponseDto checkPpn(@RequestBody PpnWithRuleSetsRequestDto requestBody) {
        return mapper.map(ruleService.checkRulesOnNotices(requestBody.getPpnList(), ruleService.getResultRulesList(requestBody.getTypeAnalyse(), requestBody.getFamilleDocumentSet(), requestBody.getRuleSet())), ResultAnalyseResponseDto.class);
    }
}


