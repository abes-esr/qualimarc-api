package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.souszoneoperator;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZonesMemeZone;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name="SOUS_ZONE_OPERATOR")
@NoArgsConstructor
public class SousZoneOperator implements Serializable {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="SOUS_ZONE")
    @NotNull
    private String sousZone;

    @Column(name="IS_PRESENT")
    @NotNull
    private boolean isPresent;

    @Column(name="OPERATEUR")
    private BooleanOperateur operateur;

    @ManyToOne
    @JoinColumn(name = "ID_PRESENCE_SOUS_ZONE_MEME_ZONE")
    private PresenceSousZonesMemeZone presenceSousZonesMemeZone;

    public SousZoneOperator(String sousZone, boolean isPresent, BooleanOperateur operateur) {
        this.sousZone = sousZone;
        this.isPresent = isPresent;
        this.operateur = operateur;
    }
    public SousZoneOperator(String sousZone, boolean isPresent) {
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }
}
