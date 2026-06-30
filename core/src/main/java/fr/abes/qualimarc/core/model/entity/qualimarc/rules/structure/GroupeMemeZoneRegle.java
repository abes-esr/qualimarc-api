package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_GROUPEMEMEZONE_REGLE")
public class GroupeMemeZoneRegle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_GROUPEMEMEZONE")
    private GroupeMemeZone groupeMemeZone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_RULE")
    @NotNull
    private SimpleRule rule;

    @Enumerated(EnumType.STRING)
    @Column(name = "OPERATOR")
    @NotNull
    private BooleanOperateur operateur;

    @Column(name = "POSITION")
    @NotNull
    private Integer position;

    public GroupeMemeZoneRegle(SimpleRule rule, BooleanOperateur operateur, Integer position, GroupeMemeZone groupeMemeZone) {
        this.rule = rule;
        this.operateur = operateur;
        this.position = position;
        this.groupeMemeZone = groupeMemeZone;
    }
}
