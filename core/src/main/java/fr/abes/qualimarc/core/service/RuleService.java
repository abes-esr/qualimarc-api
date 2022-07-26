package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.model.entity.rules.RuleSet;
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
                    if (rule.getTypeDocuments().size() == 0 || rule.getTypeDocuments().stream().anyMatch(type -> notice.getFamilleDocument().equals(type))) {
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

    /**
     * Calls the method for getting the list of rules and then passes them to the method for checking the rules on the notices
     * @param ppnList Ppns list
     * @param ruleSetList Rules set list
     * @return List<ResultRules>
     */
    public List<ResultRules> getResultRulesList(List<String> ppnList, List<String> ruleSetList) {
        RuleSet ruleSet = new RuleSet(ruleSetList);
        List<Rule> ruleList = new ArrayList<>(ruleSet.getResultRulesList());
        return checkRulesOnNotices(ppnList, ruleList);
    }

}
