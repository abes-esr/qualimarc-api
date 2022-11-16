package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeThese;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Règle complexe composée à minima d'une règle simple et de n règles liées
 * dans le cas d'une règle composée uniquement d'une règle simple, n=0
 */
@Getter @Setter
@Entity
@Table(name = "RULE")
public class ComplexRule implements Serializable {
    @Id
    @Column(name = "RULE_ID")
    private Integer id;

    @Column(name = "MESSAGE")
    @NotNull
    private String message;

    @Column(name = "PRIORITY")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Priority priority;

    //liste des types de document concernés par la règle
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "RULE_FAMILLEDOCUMENTS",
            joinColumns = @JoinColumn(name = "RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "FAMILLEDOCUMENT_ID")
    )
    private Set<FamilleDocument> famillesDocuments;

    @ElementCollection(targetClass = TypeThese.class)
    @CollectionTable(name = "RULE_TYPETHESE", joinColumns = @JoinColumn(name = "RULE_ID"))
    @Column(name = "TYPES_THESE")
    @Fetch(FetchMode.JOIN)
    @Enumerated(EnumType.STRING)
    private Set<TypeThese> typesThese;

    //liste des jeux de règles préconçus auxquels appartient la règle
    @ManyToMany
    @JoinTable(
            name = "RULE_RULESET",
            joinColumns = @JoinColumn(name = "RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "RULESET_ID")
    )
    private Set<RuleSet> ruleSet;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_FIRST_RULE")
    private SimpleRule firstRule;

    @OneToMany(mappedBy = "complexRule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OtherRule> otherRules;

    protected ComplexRule(){}

    public ComplexRule(Integer id, String message, Priority priority, Set<FamilleDocument> famillesDocuments, Set<TypeThese> typesThese, SimpleRule firstRule, List<OtherRule> otherRules) {
        this.id = id;
        this.message = message;
        this.priority = priority;
        this.famillesDocuments = famillesDocuments;
        this.typesThese = typesThese;
        this.ruleSet = new HashSet<>();
        this.firstRule = firstRule;
        this.otherRules = otherRules;
    }

    public ComplexRule(Integer id, String message, Priority priority, SimpleRule firstRule, List<OtherRule> otherRules) {
        this.id = id;
        this.message = message;
        this.priority = priority;
        this.famillesDocuments = new HashSet<>();
        this.typesThese = new HashSet<>();
        this.ruleSet = new HashSet<>();
        this.firstRule = firstRule;
        this.otherRules = otherRules;
    }

    public ComplexRule(Integer id, String message, Priority priority, Set<FamilleDocument> famillesDocuments, Set<TypeThese> typesThese, Set<RuleSet> ruleSet, SimpleRule firstRule) {
        this.id = id;
        this.message = message;
        this.priority = priority;
        this.famillesDocuments = famillesDocuments;
        this.typesThese = typesThese;
        this.ruleSet = ruleSet;
        this.firstRule = firstRule;
        this.otherRules = new LinkedList<>();
    }

    public ComplexRule(Integer id, String message, Priority priority, SimpleRule firstRule) {
        this.id = id;
        this.message = message;
        this.priority = priority;
        this.famillesDocuments = new HashSet<>();
        this.typesThese = new HashSet<>();
        this.ruleSet = new HashSet<>();
        this.firstRule = firstRule;
        this.otherRules = new LinkedList<>();
    }

    public ComplexRule(SimpleRule rule) {
        this.firstRule = rule;
        this.otherRules = new LinkedList<>();
        this.famillesDocuments = new HashSet<>();
        this.ruleSet = new HashSet<>();
    }

    public void addRuleSet(RuleSet ruleSet) {
        this.ruleSet.add(ruleSet);
    }

    public void addTypeDocument(FamilleDocument typeDocument) {
        this.famillesDocuments.add(typeDocument);
    }

    public void addTypeThese(TypeThese typeThese) { this.typesThese.add(typeThese); }

    public void addOtherRule(OtherRule otherRule) {
        this.otherRules.add(otherRule);
    }

    /**
     * Retourne true si toutes les règles qui la composent sont valides
     * @param notices notices au format xml
     * @return boolean
     */
    public boolean isValid(NoticeXml ... notices) {
        switch (notices.length) {
            case 0:
                return false;
            case 1 :
                NoticeXml notice = Arrays.stream(notices).findFirst().get();
                return isValidOneNotice(notice);
            default :
                return isValidTwoNotices(Arrays.stream(notices).findFirst().get(), Arrays.stream(notices).collect(Collectors.toList()).get(1));
        }
    }

    /**
     * Méthode permettant de vérifier une règle sur 2 notices. On teste les règles composant la règle simple sur la notice jusqu'à ce qu'on rencontre une règle de dépendance,
     * qui indique que toutes les règles suivantes s'appliqueront à la notice liée
     * @param notice notice source sur laquelle porte la notice
     * @param noticeLiee notice liée à la notice
     * @return true si la règle est valide, false sinon
     */
    private boolean isValidTwoNotices(NoticeXml notice, NoticeXml noticeLiee) {
        boolean isValid = firstRule.isValid(notice);
        boolean isDependencyFound = false;
        for (OtherRule otherRule : otherRules.stream().sorted(Comparator.comparing(OtherRule::getPosition)).collect(Collectors.toList())) {
            if (otherRule instanceof DependencyRule) {
                //dès qu'on trouve une règle de dépendance dans les linked rule, on informe le programme qu'il doit appliquer les règles suivantes sur la notice liée
                isDependencyFound = true;
                continue;
            }
            LinkedRule linkedRule = (LinkedRule) otherRule;
            switch (linkedRule.getOperateur()) {
                case ET:
                    isValid &= linkedRule.getRule().isValid((isDependencyFound) ? noticeLiee : notice);
                    break;
                case OU:
                    isValid |= linkedRule.getRule().isValid((isDependencyFound) ? noticeLiee : notice);
                    break;
                default:
                    throw new IllegalArgumentException("Operateur booléen invalide");
            }
        }
        return isValid;
    }

    private boolean isValidOneNotice(NoticeXml notice) {
        boolean isValid = firstRule.isValid(notice);
        for (OtherRule otherRule : otherRules.stream().sorted(Comparator.comparing(OtherRule::getPosition)).collect(Collectors.toList())) {
            LinkedRule linkedRule = (LinkedRule) otherRule;
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

    public List<String> getZonesFromChildren() {
        List<String> liste = new LinkedList<>();
        liste.add(this.getFirstRule().getZones());
        boolean isRuleBeforeDependance = true;
        for (OtherRule rule : this.getOtherRules()) {
            if(isRuleBeforeDependance)
                liste.add(rule.getZones());

            if(rule instanceof DependencyRule)
                isRuleBeforeDependance = false;
        }
        return liste.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Méthode permettant de récupérer l'instance d'une linked rule composant la règle complexe de type Dependency
     * @return la linkedRule correspondante, null si aucune dependency rule ne compose la complex Rule
     */
    public DependencyRule getDependencyRule() {
        Optional<OtherRule> linkedRule = this.otherRules.stream().filter(lr -> lr instanceof DependencyRule).findAny();
        return (DependencyRule) linkedRule.orElse(null);
    }


    @Override
    public int hashCode() {
        return (this.id != null) ? this.id : Objects.hash(id, message, priority, famillesDocuments, typesThese, ruleSet, firstRule, otherRules);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)){
            return true;
        }
        ComplexRule complexRule = (ComplexRule) obj;
        return this.getId().equals(complexRule.getId());
    }
}
