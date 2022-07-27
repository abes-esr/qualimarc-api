package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class Rule {
    private Integer id;
    private String message;
    private String zone;
    private Priority priority;

    //liste des types de document concernés par la règle
    private Set<String> typeDocuments;
    //liste des jeux de règles préconçus auxquels appartient la règle
    private Set<Integer> ruleSet;


    public Rule(Integer id, String message, String zone, Priority priority) {
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.typeDocuments = new HashSet<>();
        this.ruleSet = new HashSet<>();
    }

    public Rule(Integer id, String message, String zone, Priority priority, Set<String> typeDocuments) {
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.typeDocuments = typeDocuments;
        this.ruleSet = new HashSet<>();
    }

    public void addRuleSet(Integer ruleSet) {
        this.ruleSet.add(ruleSet);
    }

    public void addTypeDocument(String typeDocument) {
        this.typeDocuments.add(typeDocument);
    }


    public abstract boolean isValid(NoticeXml notice);

}
