package fr.abes.qualimarc.core.model.entity.qualimarc.reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RULESSET")
public class RuleSet {
    @Id
    @Column(name = "RULESET_ID")
    private Integer id;

    @Column(name = "LIBELLE")
    private String libelle;

    @ManyToMany(mappedBy = "ruleSet", targetEntity = ComplexRule.class)
    @JsonIgnore
    Set<ComplexRule> rules;

    public RuleSet(Integer id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }
}
