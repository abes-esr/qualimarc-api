package fr.abes.qualimarc.core.model.entity.qualimarc.reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RULESSET")
public class RuleSet implements Serializable {
    @Id
    @Column(name = "RULESET_ID")
    private Integer id;

    @Column(name = "LIBELLE")
    private String libelle;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "POSITION")
    private Integer position;

    @ManyToMany(mappedBy = "ruleSet", targetEntity = ComplexRule.class, cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ComplexRule> rules;

    public RuleSet(Integer id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public RuleSet(Integer id, String libelle, String description, Integer position) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.position = position;
    }

    public RuleSet(Integer id) {
        this.id = id;
    }
}
