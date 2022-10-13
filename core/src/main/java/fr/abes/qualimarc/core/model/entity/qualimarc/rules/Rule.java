package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pour créer un objet règle, il est nécessaire d'implémenter la méthode abstraite isValid
 * Conceptuellement, une règle est valide lorsque les conditions qu'elle décrit sont réunies dans la notice, et si c'est le cas, on retourne le message
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Rule {
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

    //liste des jeux de règles préconçus auxquels appartient la règle
    @ManyToMany
    @JoinTable(
            name = "RULE_RULESET",
            joinColumns = @JoinColumn(name = "RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "RULESET_ID")
    )
    private Set<RuleSet> ruleSet;


    public Rule(Integer id, String message, Priority priority) {
        this.id = id;
        this.message = message;
        this.priority = priority;
        this.famillesDocuments = new HashSet<>();
        this.ruleSet = new HashSet<>();
    }

    public Rule(Integer id, String message, Priority priority, Set<FamilleDocument> famillesDocuments) {
        this.id = id;
        this.message = message;
        this.priority = priority;
        this.famillesDocuments = famillesDocuments;
        this.ruleSet = new HashSet<>();
    }

    public void addRuleSet(RuleSet ruleSet) {
        this.ruleSet.add(ruleSet);
    }

    public void addTypeDocument(FamilleDocument typeDocument) {
        this.famillesDocuments.add(typeDocument);
    }


    public abstract boolean isValid(NoticeXml notice);

    public abstract List<String> getZonesFromChildren();


}
