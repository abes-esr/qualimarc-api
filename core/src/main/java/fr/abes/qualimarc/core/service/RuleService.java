package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.repository.rules.RulesRepository;
import fr.abes.qualimarc.core.repository.rules.RulesRepositoryImpl;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.abes.qualimarc.core.utils.Priority.P1;

@Service
public class RuleService {
    @Autowired
    private NoticeBibioService serviceBibio;

    @Autowired
    private RulesRepository rulesRepository;

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
     * Method that returns rules associated with the analyse type chosen
     *
     * @param typeAnalyse : priority to look for in rules
     * @param typeDocument : list of document type to look for in rules
     * @param ruleSet set of rules to look for in rules
     * @return list of rules according to given parameters
     */
    public Set<Rule> getResultRulesList(TypeAnalyse typeAnalyse, Set<String> typeDocument, Set<Integer> ruleSet) {
        //cas analyse rapide ou experte
        switch (typeAnalyse) {
            case QUICK:
                return rulesRepository.findByPriority(Priority.P1);
            case COMPLETE:
                return rulesRepository.findAll();
            case FOCUSED:
                //cas d'une analyse ciblée, on récupère les règles en fonction des types de documents et des ruleSet
                if ((typeDocument == null || typeDocument.size() == 0) && (ruleSet == null || ruleSet.size() == 0))
                    throw new IllegalRulesSetException("Impossible de lancer l'analysée ciblée sans paramètres supplémentaires");
                Set<Rule> result = new HashSet<>();
                if (typeDocument != null)
                    typeDocument.forEach(t -> result.addAll(rulesRepository.findByTypeDocument(t)));
                if (ruleSet != null)
                    ruleSet.forEach(r -> result.addAll(rulesRepository.findByRuleSet(r)));
                return result;
            default:
                throw new IllegalRulesSetException("Jeu de règle inconnu");
        }
    }
}
