package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Règle complexe composée à minima d'une règle simple et de n règles liées
 */
@Getter @Setter
@Entity
public class ComplexRule extends Rule implements Serializable {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_FIRST_RULE")
    private SimpleRule firstRule;

    @OneToMany(mappedBy = "complexRule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LinkedRule> otherRules;

    protected ComplexRule(){}

    public ComplexRule(Integer id, String message, Priority priority, Set<FamilleDocument> famillesDocuments, SimpleRule firstRule, List<LinkedRule> otherRules) {
        super(id, message, priority, famillesDocuments);
        this.firstRule = firstRule;
        this.otherRules = otherRules;
    }

    public ComplexRule(Integer id, String message, Priority priority, SimpleRule firstRule, List<LinkedRule> otherRules) {
        super(id, message, priority);
        this.firstRule = firstRule;
        this.otherRules = otherRules;
    }

    public ComplexRule(Integer id, String message, Priority priority, Set<FamilleDocument> famillesDocuments, SimpleRule firstRule) {
        super(id, message, priority, famillesDocuments);
        this.firstRule = firstRule;
        this.otherRules = new LinkedList<>();
    }

    public ComplexRule(Integer id, String message, Priority priority, SimpleRule firstRule) {
        super(id, message, priority);
        this.firstRule = firstRule;
        this.otherRules = new LinkedList<>();
    }

    public ComplexRule(SimpleRule rule) {
        this.firstRule = rule;
    }

    public void addOtherRule(LinkedRule linkedRule) {
        this.otherRules.add(linkedRule);
    }

    /**
     * Retourne true si toutes les règles qui la composent sont valides
     * @param notice
     * @return
     */
    @Override
    public boolean isValid(NoticeXml notice) {
        boolean isValid = firstRule.isValid(notice);
        for (LinkedRule linkedRule : otherRules) {
            switch (linkedRule.getOperateur()) {
                case ET:
                    //équivalent à isValid && linkedRule.getRule().isValid(notice)
                    isValid &= linkedRule.getRule().isValid(notice);
                    break;
                case OU:
                    //équivalent à isValid || linkedRule.getRule().isValid(notice)
                    isValid |= linkedRule.getRule().isValid(notice);
                    break;
                default:
                    throw new IllegalArgumentException("Operateur booléen invalide");
            }
        }
        return isValid;
    }

    @Override
    public List<String> getZonesFromChildren() {
        List liste = new LinkedList();
        liste.add(this.getFirstRule().getZones());
        this.getOtherRules().forEach(rule -> {
            liste.add(rule.getRule().getZones());
        });
        return liste;
    }
}
