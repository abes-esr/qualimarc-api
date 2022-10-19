package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.chainecaracteres;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.EnumChaineCaracteres;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Classe qui définie une chaine de caractères, son opérateur logique (ET/OU) et sa méthode de recherche dans la zone a tester (UNIQUEMENT, CONTIENT, COMMENCE, TERMINE)
 */
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
    private EnumChaineCaracteres enumChaineCaracteres;

    @Column(name = "CHAINE_CARACTERES")
    @NotNull
    private String chaineCaracteres;

    @ManyToOne
    @JoinColumn(name = "ID_CHAINE_CARACTERES")
    private PresenceChaineCaracteres presenceChaineCaracteres;

    public ChaineCaracteres(BooleanOperateur booleanOperateur, EnumChaineCaracteres enumChaineCaracteres, String chaineCaracteres) {
        this.booleanOperateur = booleanOperateur;
        this.enumChaineCaracteres = enumChaineCaracteres;
        this.chaineCaracteres = chaineCaracteres;
    }
}
