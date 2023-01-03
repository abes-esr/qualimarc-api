package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import fr.abes.qualimarc.core.exception.noticexml.ZoneNotFoundException;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.OtherRule;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRule;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.repository.qualimarc.ComplexRulesRepository;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import fr.abes.qualimarc.core.utils.TypeNoticeLiee;
import fr.abes.qualimarc.core.utils.TypeThese;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RuleService {
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private ComplexRulesRepository complexRulesRepository;

    @Autowired
    @Qualifier("asyncExecutor")
    private Executor asyncExecutor;

    private AtomicInteger cn = new AtomicInteger(0);

    @Async("asyncExecutor")
    public CompletableFuture<ResultAnalyse> checkRulesOnNotices(Set<ComplexRule> rulesList, List<String> ppns) {
        ResultAnalyse resultAnalyse = new ResultAnalyse();
        log.debug("Handling list of " + ppns.size() + " ppn");
        for (String ppn : ppns) {
            boolean isOk = true;
            ResultRules result = new ResultRules(ppn);
            try {
                NoticeXml noticeSource = noticeService.getBiblioByPpn(ppn);
                if (noticeSource.isDeleted()) {
                    resultAnalyse.addPpnInconnu(ppn);
                } else {
                    for (ComplexRule rule : rulesList) {
                        resultAnalyse.addPpnAnalyse(ppn);
                        if (isRuleAppliedToNotice(noticeSource, rule)) {
                            OtherRule dependencyRule = rule.getDependencyRule();
                            if (dependencyRule != null) {
                                //il existe une règle de dépendance dans la règle complexe
                                //récupération de la notice liée
                                Set<String> ppnNoticeLiee = rule.getDependencyRule().getPpnsNoticeLiee(noticeSource);
                                for (String ppnLie : ppnNoticeLiee) {
                                    NoticeXml noticeLiee = (rule.getDependencyRule().getTypeNoticeLiee() == TypeNoticeLiee.AUTORITE) ? noticeService.getAutoriteByPpn(ppnLie) : noticeService.getBiblioByPpn(ppnLie);
                                    if (noticeLiee != null) {
                                        isOk &= constructResultRuleOnNotice(result, rule, noticeSource, noticeLiee);
                                    }
                                }
                            } else {
                                isOk &= constructResultRuleOnNotice(result, rule, noticeSource);
                            }
                        }
                    }
                    if (isOk) {
                        resultAnalyse.addPpnOk(ppn);
                    } else {
                        resultAnalyse.addPpnErrone(ppn);
                        resultAnalyse.addResultRule(result);
                    }
                }
            } catch (SQLException | IOException ex) {
                result.addMessage("Erreur d'accès à la base de données sur PPN : " + ppn);
                resultAnalyse.addPpnInconnu(ppn);
                resultAnalyse.addResultRule(result);
            } catch (IllegalPpnException | IllegalTypeDocumentException ex) {
                resultAnalyse.addPpnInconnu(ppn);
                result.addMessage(ex.getMessage());
                resultAnalyse.addResultRule(result);
            }
            this.cn.addAndGet(1);
        }
        return CompletableFuture.completedFuture(resultAnalyse);
    }

    @SneakyThrows
    private boolean constructResultRuleOnNotice(ResultRules result, ComplexRule rule, NoticeXml... notices) {
        NoticeXml notice = notices[0];
        result.setFamilleDocument(referenceService.getFamilleDocument(notice.getFamilleDocument()));
        result.setTypeThese(notice.getTypeThese());
        try {
            result.setTitre(notice.getTitre());
        } catch (ZoneNotFoundException e) {
            result.setTitre(e.getMessage());
        }
        try {
            result.setAuteur(notice.getAuteur());
        } catch (ZoneNotFoundException e) {
            result.setAuteur(e.getMessage());
        }
        result.setIsbn(notice.getIsbn());
        result.setOcn(notice.getOcn());
        result.setDateModification(notice.getDateModification());
        result.setRcr(notice.getRcr());
        if (notices.length == 1) {//si la règle est valide, alors on renvoie le message
            if (rule.isValid(notice)) {
                ResultRule resultRule = new ResultRule(rule.getId(), rule.getPriority(), rule.getMessage());
                //on ajoute toutes les zones concernées par la règle au jeu de résultat
                rule.getZonesFromChildren().forEach(resultRule::addZone);
                result.addDetailErreur(resultRule);
                return false;
            }
            return true;
        }
        if (rule.isValid(notice, notices[1])) {
            ResultRule resultRule = new ResultRule(rule.getId(), rule.getPriority(), rule.getMessage() + " PPN lié : " + notices[1].getPpn());
            //on ajoute toutes les zones concernées par la règle au jeu de résultat
            rule.getZonesFromChildren().forEach(resultRule::addZone);
            result.addDetailErreur(resultRule);
            return false;
        }
        return true;
    }

    public boolean isRuleAppliedToNotice(NoticeXml notice, ComplexRule rule) {
        //si pas de type de document renseigné, la règle est appliquée quoi qu'il arrive
        if (rule.getFamillesDocuments().size() == 0) {
            //on force la vérification sur la famille de document pour lever une exception le cas échéant
            if (notice.getFamilleDocument() != "" && rule.getTypesThese().size() != 0) {
                return rule.getTypesThese().stream().anyMatch(tt -> tt.equals(notice.getTypeThese()));
            }
            return true;
        }
        //si l'un des types de doc de la règle matche avec le type de la notice
        if (rule.getFamillesDocuments().stream().anyMatch(type -> notice.getFamilleDocument().equals(type.getId()))) {
            return true;
        } else {
            if (rule.getTypesThese().size() != 0) {
                return rule.getTypesThese().stream().anyMatch(tt -> tt.equals(notice.getTypeThese()));
            }
        }
        return false;
    }

    public void save(ComplexRule rule) {
        complexRulesRepository.save(rule);
    }


    /**
     * Method that returns rules associated with the analyse type chosen
     *
     * @param typeAnalyse      : priority to look for in rules
     * @param familleDocuments : list of document type to look for in rules
     * @param ruleSet          set of rules to look for in rules
     * @return list of rules according to given parameters
     */
    public Set<ComplexRule> getResultRulesList(TypeAnalyse typeAnalyse, Set<FamilleDocument> familleDocuments, Set<TypeThese> typeThese, Set<RuleSet> ruleSet) {
        //cas analyse rapide ou experte
        switch (typeAnalyse) {
            case QUICK:
                return complexRulesRepository.findByPriority(Priority.P1);
            case COMPLETE:
                return new HashSet<>(complexRulesRepository.findAll());
            case FOCUS:
                //cas d'une analyse ciblée, on récupère les règles en fonction des types de documents et des ruleSet
                if ((familleDocuments == null || familleDocuments.isEmpty()) && (typeThese == null || typeThese.isEmpty()) && (ruleSet == null || ruleSet.isEmpty()))
                    throw new IllegalRulesSetException("Impossible de lancer l'analysée ciblée sans paramètres supplémentaires");
                Set<ComplexRule> rulesTypes = new HashSet<>();
                Set<ComplexRule> rulesRuleSet = new HashSet<>();
                if (familleDocuments != null)
                    familleDocuments.forEach(t -> rulesTypes.addAll(complexRulesRepository.findByFamillesDocuments(t)));
                if (typeThese != null)
                    typeThese.forEach(t -> rulesTypes.addAll(complexRulesRepository.findByTypesThese(t)));
                if (ruleSet != null)
                    ruleSet.forEach(r -> rulesRuleSet.addAll(complexRulesRepository.findByRuleSet(r)));

                if (!rulesRuleSet.isEmpty() && !rulesTypes.isEmpty()) {
                    //on retourne l'intersection entre la liste contenant les règles par familles de doc et celle par jeux de règles personnalisés
                    return rulesTypes.stream().filter(rulesRuleSet::contains).collect(Collectors.toSet());
                }
                if(!rulesRuleSet.isEmpty())
                    return rulesRuleSet;

                return rulesTypes;
            default:
                throw new IllegalRulesSetException("Jeu de règle inconnu");
        }
    }

    @Transactional
    public void saveAll(List<ComplexRule> rules) throws IllegalArgumentException {
        for (ComplexRule rule : rules) {
            try {
                this.complexRulesRepository.save(rule);
            } catch (JpaObjectRetrievalFailureException ex) {
                //exception levée dans le cas ou un type de document n'est pas connu
                throw new IllegalArgumentException("Type de document " + ex.getCause().getMessage() + " inconnu sur règle : " + rule.getId(), ex);
            }
        }
    }

    public void viderRegles() {
        this.complexRulesRepository.deleteAll();
    }

    public double getCn(int nbTotal) {
        if (nbTotal != 0)
            return ((double) this.cn.get() / (double) (nbTotal) * 100);
        return 0;
    }

    public void resetCn() {
        this.cn = new AtomicInteger();
    }

    public List<ComplexRule> getAllComplexRules() {
        return this.complexRulesRepository.findAll();
    }

}
