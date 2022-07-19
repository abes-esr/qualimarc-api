package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.requestToCheck.FileToCheck;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.model.entity.rules.RuleSet;
import fr.abes.qualimarc.core.model.entity.rules.rulesSet.RulesSetType;
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

    //    Ancienne méthode d'applications des règles sur les ppn
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
     * Method that controls a set of rules on a notice
     * @param filesToCheck objet contenant les ppn et les jeux de règles associées
     * @return return a list of ResultRules
     */
    public List<ResultRules> checkRulesOnFiles(List<FileToCheck> filesToCheck) {
        List<ResultRules> resultRules = new ArrayList<>();

        for (FileToCheck fileToCheck : filesToCheck
        ) {
            String ppn = fileToCheck.getPpn();
            ResultRules result = new ResultRules(ppn);

            for (RulesSetType rulesSetType : fileToCheck.getRulesSetType()
            ) {
                RuleSet ruleSet = new RuleSet();
                List<Rule> rulesList = ruleSet.getRuleList(rulesSetType);

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
        }
        return resultRules;
    }

}
