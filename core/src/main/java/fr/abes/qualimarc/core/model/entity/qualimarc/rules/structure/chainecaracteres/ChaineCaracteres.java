package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.chainecaracteres;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.EnumChaineDeCaracteres;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "CHAINE_CARACTERES")
public class ChaineCaracteres implements Serializable {

    @Id
    @Column(name = "ID")
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name= "BOOLEAN_OPERATOR")
    @NotNull
    private BooleanOperateur booleanOperateur;

    @Column(name = "ENUM_CHAINE_CARACTERES")
    @NotNull
    private EnumChaineDeCaracteres enumChaineDeCaracteres;

    @Column(name = "CHAINE_CARACTERES")
    @NotNull
    private String chaineCaracteres;

    @ManyToOne
    @JoinColumn(name = "ID_CHAINE_CARACTERES")
    private PresenceChaineCaracteres presenceChaineCaracteres;

    public ChaineCaracteres(BooleanOperateur booleanOperateur, EnumChaineDeCaracteres enumChaineDeCaracteres, String chaineCaracteres) {
        this.booleanOperateur = booleanOperateur;
        this.enumChaineDeCaracteres = enumChaineDeCaracteres;
        this.chaineCaracteres = chaineCaracteres;
    }
}
