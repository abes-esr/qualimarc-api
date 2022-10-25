package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.PresenceChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Classe qui définie une chaine de caractères et son opérateur logique (ET/OU)
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
    private BooleanOperateur booleanOperateur;

    @Column(name = "CHAINE_CARACTERES")
    @NotNull
    private String chaineCaracteres;

    @ManyToOne
    @JoinColumn(name = "ID_CHAINE_CARACTERES")
    private PresenceChaineCaracteres presenceChaineCaracteres;

    /**
     * Constructeur sans opérateur
     * @param chaineCaracteres chaines de caractères à rechercher
     */
    public ChaineCaracteres(String chaineCaracteres) {
        this.chaineCaracteres = chaineCaracteres;
    }

    /**
     * Constructeur avec opérateur
     * @param booleanOperateur opérateur logique pour l'enchainement des chaines de caractères
     * @param chaineCaracteres chaines de caractères à rechercher
     */
    public ChaineCaracteres(BooleanOperateur booleanOperateur, String chaineCaracteres) {
        this.booleanOperateur = booleanOperateur;
        this.chaineCaracteres = chaineCaracteres;
    }
}
