package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import fr.abes.qualimarc.web.dto.ControllingPpnWithRuleSetsRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class PublicController extends AbstractController {
    @Autowired
    private NoticeBibioService service;

    @Autowired
    private RuleService ruleService;

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return service.getByPpn(ppn);
    }

    @PostMapping(value = "/check", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkPpn(@Valid @RequestBody ControllingPpnWithRuleSetsRequestDto requestBody) {
        return buildResponseEntity(ruleService.checkRulesOnNotices(requestBody.getPpnList(), ruleService.getResultRulesList(Enum.valueOf(TypeAnalyse.class, requestBody.getTypeAnalyse()), requestBody.getFamillesDocuments(), requestBody.getRules())));
    }
}


