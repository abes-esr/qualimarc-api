package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.exception.noticexml.AuteurNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.IsbnNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.TitreNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.ZoneNotFoundException;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.repository.qualimarc.RulesRepository;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RuleService {
    @Autowired
    private NoticeBibioService serviceBibio;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private RulesRepository rulesRepository;

    public ResultAnalyse checkRulesOnNotices(List<String> ppns, Set<Rule> rulesList) {
        ResultAnalyse resultAnalyse = new ResultAnalyse();
        for (String ppn : ppns) {
            boolean isOk = true;
            ResultRules result = new ResultRules(ppn);
            try {
                NoticeXml notice = serviceBibio.getByPpn(ppn);
                resultAnalyse.addPpnAnalyse(ppn);
                for (Rule rule : rulesList) {
                    if (isRuleAppliedToNotice(notice, rule)) {
                        isOk &= constructResultRuleOnNotice(result, notice, rule);
                    }
                }
                if (isOk) {
                    resultAnalyse.addPpnOk(ppn);
                } else {
                    resultAnalyse.addPpnErrone(ppn);
                    resultAnalyse.addResultRule(result);
                }
            } catch (SQLException | IOException ex) {
                result.addMessage("Erreur d'accès à la base de données sur PPN : " + ppn);
                resultAnalyse.addPpnInconnu(ppn);
            } catch (IllegalPpnException ex) {
                resultAnalyse.addPpnInconnu(ppn);
                result.addMessage(ex.getMessage());
            }
        }
        return resultAnalyse;
    }

    private boolean constructResultRuleOnNotice(ResultRules result, NoticeXml notice, Rule rule) {
        result.setFamilleDocument(referenceService.getFamilleDocument(notice.getFamilleDocument()));
        try {
            result.setTitre(notice.getTitre());
        } catch ( ZoneNotFoundException e) {
            result.setTitre(e.getMessage());
        }
        try {
            result.setAuteur(notice.getAuteur());
        }catch ( ZoneNotFoundException e){
            result.setAuteur(e.getMessage());
        }
        try {
            result.setIsbn(notice.getIsbn());
        }catch ( ZoneNotFoundException e){
            result.setIsbn(e.getMessage());
        }

        //si la règle est valide, alors on renvoie le message
        if (rule.isValid(notice)) {
            result.addMessage(rule.getMessage());
            return false;
        }
        return true;
    }

    public boolean isRuleAppliedToNotice(NoticeXml notice, Rule rule) {
        if (rule.getFamillesDocuments().size() == 0 || rule.getFamillesDocuments().stream().anyMatch(type -> notice.getFamilleDocument().equals(type.getId())))
            return true;
        if (notice.isTheseSoutenance() && rule.getFamillesDocuments().stream().anyMatch(type -> type.getId().equals("TS")))
            return true;
        if (notice.isTheseRepro() && rule.getFamillesDocuments().stream().anyMatch(type -> type.getId().equals("TR")))
            return true;
        return false;
    }

    public void save(Rule rule) {
        rulesRepository.save(rule);
    }


    /**
     * Method that returns rules associated with the analyse type chosen
     *
     * @param typeAnalyse      : priority to look for in rules
     * @param familleDocuments : list of document type to look for in rules
     * @param ruleSet          set of rules to look for in rules
     * @return list of rules according to given parameters
     */
    public Set<Rule> getResultRulesList(TypeAnalyse typeAnalyse, Set<FamilleDocument> familleDocuments, Set<RuleSet> ruleSet) {
        //cas analyse rapide ou experte
        switch (typeAnalyse) {
            case QUICK:
                return rulesRepository.findByPriority(Priority.P1);
            case COMPLETE:
                return new HashSet<>(rulesRepository.findAll());
            case FOCUSED:
                //cas d'une analyse ciblée, on récupère les règles en fonction des types de documents et des ruleSet
                if ((familleDocuments == null || familleDocuments.size() == 0) && (ruleSet == null || ruleSet.size() == 0))
                    throw new IllegalRulesSetException("Impossible de lancer l'analysée ciblée sans paramètres supplémentaires");
                Set<Rule> result = new HashSet<>();
                if (familleDocuments != null)
                    familleDocuments.forEach(t -> result.addAll(rulesRepository.findByFamillesDocuments(t)));
                if (ruleSet != null)
                    ruleSet.forEach(r -> result.addAll(rulesRepository.findByRuleSet(r)));
                return result;
            default:
                throw new IllegalRulesSetException("Jeu de règle inconnu");
        }
    }

    @Transactional
    public void saveAll(List<Rule> rules) throws IllegalArgumentException {
        for (Rule rule : rules) {
            try {
                this.rulesRepository.save(rule);
            } catch (JpaObjectRetrievalFailureException ex) {
                //exception levée dans le cas ou un type de document n'est pas connu
                throw new IllegalArgumentException("Type de document " + ex.getCause().getMessage() + " inconnu sur règle : " + rule.getId(), ex);
            }
        }
    }
}
