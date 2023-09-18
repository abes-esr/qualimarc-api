package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.PresenceChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.TypeVerification;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "POSITION")
    private Integer position;

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
     * @param position position de la chaine de caractères dans la liste de chaines de caractères
     * @param chaineCaracteres chaines de caractères à rechercher
     */
    public ChaineCaracteres(int position, String chaineCaracteres, PresenceChaineCaracteres presenceChaineCaracteres) {
        this.position = position;
        this.chaineCaracteres = chaineCaracteres;
        this.presenceChaineCaracteres = presenceChaineCaracteres;
    }

    /**
     * Constructeur avec opérateur
     * @param position position de la chaine de caractères dans la liste de chaines de caractères
     * @param booleanOperateur opérateur logique pour l'enchainement des chaines de caractères
     * @param chaineCaracteres chaines de caractères à rechercher
     */
    public ChaineCaracteres(int position, BooleanOperateur booleanOperateur, String chaineCaracteres, PresenceChaineCaracteres presenceChaineCaracteres) {
        this.position = position;
        this.booleanOperateur = booleanOperateur;
        this.chaineCaracteres = chaineCaracteres;
        this.presenceChaineCaracteres = presenceChaineCaracteres;
    }

    public boolean isValid(String value, TypeVerification typeVerification){
        value = (value != null) ? value : ""; //meme traitement de null que si c'etait une chaine vide
        switch (typeVerification) {
            case STRICTEMENT:
                return value.equals(chaineCaracteres);
            case COMMENCE:
                return value.startsWith(chaineCaracteres);
            case TERMINE:
                return value.endsWith(chaineCaracteres);
            case CONTIENT:
                return value.contains(chaineCaracteres);
            case NECONTIENTPAS:
                return !value.contains(chaineCaracteres);
            default:
                return false;
        }
    }
}
