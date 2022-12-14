package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.utils.BooleanOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Règle liée composée d'une règle simple et d'un opérateur pour pouvoir la conditionner avec la règle précédente d'une liste
 * L'ordre d'exécution des règles liées est géré par l'attribut position
 */
@Getter @Setter
@NoArgsConstructor
@Entity
public class LinkedRule extends OtherRule {

    @OneToOne(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "RULE_ID")
    private SimpleRule rule;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "OPERATOR")
    private BooleanOperateur operateur;

    public LinkedRule(SimpleRule rule, BooleanOperateur operateur, Integer position, ComplexRule complexRule) {
        super(rule.getId(), position, complexRule);
        this.rule = rule;
        this.rule.setComplexRule(complexRule);
        this.operateur = operateur;
    }

    @Override
    public List<String> getZones() {
        return this.getRule().getZones();
    }
}
