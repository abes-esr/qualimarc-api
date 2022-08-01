package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "RULE")
public abstract class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RULE_ID")
    private Integer id;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "ZONE")
    private String zone;

    @Column(name = "PRIORITY")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    //liste des types de document concernés par la règle
    @ManyToMany
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


    public Rule(Integer id, String message, String zone, Priority priority) {
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.famillesDocuments = new HashSet<>();
        this.ruleSet = new HashSet<>();
    }

    public Rule(Integer id, String message, String zone, Priority priority, Set<FamilleDocument> famillesDocuments) {
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.famillesDocuments = famillesDocuments;
        this.ruleSet = new HashSet<>();
    }

    public Rule(String message, String zone, Priority priority) {
        this.message = message;
        this.zone = zone;
        this.priority = priority;
    }

    public Rule(String message, String zone, Priority priority, Set<FamilleDocument> famillesDocuments) {
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.famillesDocuments = famillesDocuments;
    }

    public void addRuleSet(RuleSet ruleSet) {
        this.ruleSet.add(ruleSet);
    }

    public void addTypeDocument(FamilleDocument typeDocument) {
        this.famillesDocuments.add(typeDocument);
    }


    public abstract boolean isValid(NoticeXml notice);

}
