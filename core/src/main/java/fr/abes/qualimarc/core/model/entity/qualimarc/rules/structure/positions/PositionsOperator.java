package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.positions;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PositionSousZone;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "POSITION_OPERATOR")
public class PositionsOperator implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "POSITION")
    private Integer position = 0;

    @Column(name= "COMPARAISON_OPERATOR")
    @Enumerated(EnumType.STRING)
    private ComparaisonOperateur comparaisonOperateur;

    @ManyToOne
    @JoinColumn(name = "ID_POSITION_SOUSZONE")
    private PositionSousZone positionSousZone;

    public PositionsOperator(Integer position, ComparaisonOperateur comparaisonOperateur, PositionSousZone positionSousZone) {
        this.position = position;
        this.comparaisonOperateur = comparaisonOperateur;
        this.positionSousZone = positionSousZone;
    }

    public PositionsOperator(Integer position, ComparaisonOperateur comparateur) {
        this.position = position;
        this.comparaisonOperateur = comparateur;
    }

    public PositionsOperator(Integer id, Integer position, ComparaisonOperateur comparaisonOperateur) {
        this.id = id;
        this.position = position;
        this.comparaisonOperateur = comparaisonOperateur;
    }
}
