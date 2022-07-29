package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
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
public abstract class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_Sequence")
    @SequenceGenerator(name = "rule_Sequence", sequenceName = "RULE_SEQ", allocationSize = 1)
    @Column(name = "RULE_ID")
    private Integer id;
    @Column(name = "MESSAGE")
    private String message;
    @Column(name = "ZONE")
    private String zone;
    @Column(name = "PRIORITY")
    private Priority priority;

    //liste des types de document concernés par la règle
    private Set<String> famillesDocuments;
    //liste des jeux de règles préconçus auxquels appartient la règle
    private Set<Integer> ruleSet;


    public Rule(Integer id, String message, String zone, Priority priority) {
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.famillesDocuments = new HashSet<>();
        this.ruleSet = new HashSet<>();
    }

    public Rule(Integer id, String message, String zone, Priority priority, Set<String> famillesDocuments) {
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.famillesDocuments = famillesDocuments;
        this.ruleSet = new HashSet<>();
    }

    public void addRuleSet(Integer ruleSet) {
        this.ruleSet.add(ruleSet);
    }

    public void addTypeDocument(String typeDocument) {
        this.famillesDocuments.add(typeDocument);
    }


    public abstract boolean isValid(NoticeXml notice);

}
