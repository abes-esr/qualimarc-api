package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RuleService {
    @Autowired
    private NoticeBibioService serviceBibio;

    public List<ResultRules> checkRulesOnNotices(List<String> ppns, List<Rule> rulesList) {
        List<ResultRules> resultRules = new ArrayList<>();
        for (String ppn : ppns) {
            ResultRules result = new ResultRules(ppn);
            try {
                NoticeXml notice = serviceBibio.getByPpn(ppn);
                for (Rule rule : rulesList) {
                    if (isRuleAppliedToNotice(notice, rule)) {
                        if (!rule.isValid(notice)) {
                            result.addMessage(rule.getMessage());
                        }
                    }
                }
            } catch (SQLException | IOException ex) {
                result.addMessage("Erreur d'accès à la base de données sur PPN : " + ppn);
            } catch (IllegalPpnException ex) {
                result.addMessage(ex.getMessage());
            } finally {
                resultRules.add(result);
            }
        }
        return resultRules;
    }

    public boolean isRuleAppliedToNotice(NoticeXml notice, Rule rule) {
        if (rule.getTypeDocuments().size() == 0 || rule.getTypeDocuments().stream().anyMatch(type -> notice.getFamilleDocument().equals(type)))
            return true;
        if (notice.isTheseSoutenance() && rule.getTypeDocuments().stream().anyMatch(type -> type.equals("TS")))
            return true;
        if (notice.isTheseRepro() && rule.getTypeDocuments().stream().anyMatch(type -> type.equals("TR")))
            return true;
        return false;
    }
}

