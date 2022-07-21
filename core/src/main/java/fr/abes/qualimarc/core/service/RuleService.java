package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.requestToCheck.RequestToCheck;
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
import java.util.stream.Collectors;

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
     * @param requestToCheck object that contains the lists of ppn and ruleset
     * @return return a list of ResultRules
     */
    public List<ResultRules> getResultRulesList(RequestToCheck requestToCheck) {

        List<Rule> ruleList = new ArrayList<>();

        //  Checks if a rule set appears more than once and if so throws an exception.
        //  If not, calls the methods that get the list of rules corresponding to the requested ruleset
        if(requestToCheck.getRulesSetList().stream().filter(i -> i.equals("P1")).count() == 1){
            //  Calls up the quick rule handling methods
            RuleSet ruleSet = new RuleSet("P1");
            List<Rule> tempBasicList = new ArrayList<>(ruleSet.getQuickRulesList());
            ruleList.addAll(tempBasicList);
        } else if (requestToCheck.getRulesSetList().stream().filter(i -> i.equals("P2")).count() == 1) {
            //  Calls up the quick rule handling methods
            RuleSet ruleSet = new RuleSet();
            List<Rule> tempBasicList = new ArrayList<>(ruleSet.getQuickRulesList());
            ruleList.addAll(tempBasicList);
            //  Calls up the advanced rule handling methods
            List<Rule> tempAdvancedList = new ArrayList<>(ruleSet.getAdvancedRulesList());
            ruleList.addAll(tempAdvancedList);
        } else if (requestToCheck.getRulesSetList().stream().filter(i -> i.equals("P3")).count() == 1) {
            //  Calls up the focused rules handling methods
            RuleSet ruleSet = new RuleSet();
            List<Rule> tempFocusedList = new ArrayList<>(ruleSet.getFocusedRulesList(requestToCheck.getRulesSetList()));
            ruleList.addAll(tempFocusedList);
        } else throw new IllegalRulesSetException("Type de jeu de règles inconnu");

        return checkRulesOnNotices(requestToCheck.getPpnList(), ruleList);
    }

}
