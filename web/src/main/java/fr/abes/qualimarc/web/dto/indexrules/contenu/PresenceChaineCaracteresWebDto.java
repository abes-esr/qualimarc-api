package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe WebDto qui définie une règle permettant de tester la présence d'une zone et sous-zone ainsi que la présence,
 * la position ou la conformité d'une ou plusieurs chaines de caractères dans une sous-zone.
 *
 * Indexation des positions:
 * - Les champs positionstart et positionend sont en index base 0.
 * - 0 correspond au premier caractere.
 * - Exemple: positionstart=3 et positionend=3 cible le 4e caractere.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("presencechainecaracteres")
public class PresenceChaineCaracteresWebDto extends SimpleRuleWebDto {

    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    @JsonProperty("souszone")
    private String sousZone;

    @Pattern(regexp = "STRICTEMENT|CONTIENT|COMMENCE|TERMINE|NECONTIENTPAS", message = "Le champ niveau de verification ne peut contenir que STRICTEMENT, CONTIENT, COMMENCE, TERMINE, NECONTIENTPAS")
    @JsonProperty("type-de-verification")
    @NotNull
    private String typeDeVerification;

    @JsonProperty("chaines-caracteres")
    private List<ChaineCaracteresWebDto> listChaineCaracteres;

    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ position ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("position")
    private String position;

    // Index base 0: 0 = premier caractere.
    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionstart ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionstart")
    private String positionStart;

    // Index base 0: 0 = premier caractere.
    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionend ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionend")
    private String positionEnd;

    /**
     * Constructeur sans liste de chaines de caractères
     * @param id identifiant de la règle
     * @param idExcel identifiant excel de la règle
     * @param message message à renvoyer si la règle est vérifiée
     * @param zone zone sur laquelle appliquer la règle
     * @param priority priorité de la règle
     * @param typesDoc type de document de la notice sur laquelle appliquer la règle
     * @param sousZone sous-zone sur laquelle appliquer la règle
     * @param typeDeVerifications type de vérification à appliquer pour la règle
     */
    public PresenceChaineCaracteresWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message,  boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String typeDeVerifications, List<ChaineCaracteresWebDto> listChaineCaracteres) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerifications;
        this.listChaineCaracteres = listChaineCaracteres;
    }

    /**
     * Constructeur sans liste de chaines de caractères
     * @param id identifiant de la règle
     * @param idExcel identifiant excel de la règle
     * @param message message à renvoyer si la règle est vérifiée
     * @param zone zone sur laquelle appliquer la règle
     * @param priority priorité de la règle
     * @param typesDoc type de document de la notice sur laquelle appliquer la règle
     * @param sousZone sous-zone sur laquelle appliquer la règle
     * @param positionStart position dans la sous-zone où commence le contrôle
     * @param positionEnd position dans la sous-zone où s'arrête le contrôle
     * @param typeDeVerifications type de vérification à appliquer pour la règle
     */
    public PresenceChaineCaracteresWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message,  boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String positionStart, String positionEnd, String typeDeVerifications, List<ChaineCaracteresWebDto> listChaineCaracteres) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.typeDeVerification = typeDeVerifications;
        this.listChaineCaracteres = listChaineCaracteres;
    }

    /**
     * Constructeur sans liste de chaines de caractères
     * @param id identifiant de la règle
     * @param idExcel identifiant excel de la règle
     * @param message message à renvoyer si la règle est vérifiée
     * @param zone zone sur laquelle appliquer la règle
     * @param priority priorité de la règle
     * @param typesDoc type de document de la notice sur laquelle appliquer la règle
     * @param sousZone sous-zone sur laquelle appliquer la règle
     * @param positionStart position dans la sous-zone où commence le contrôle
     * @param positionEnd position dans la sous-zone où s'arrête le contrôle
     * @param typeDeVerifications type de vérification à appliquer pour la règle
     */
    public PresenceChaineCaracteresWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String typeDeVerifications, String position, String positionStart, String positionEnd, List<ChaineCaracteresWebDto> listChaineCaracteres) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.position = position;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.typeDeVerification = typeDeVerifications;
        this.listChaineCaracteres = listChaineCaracteres;
    }

    /**
     * Constructeur avec liste de chaines de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la règle
     * @param booleanOperator opérateur logique qui définit l'enchainement des règles
     * @param sousZone sous-zone sur laquelle appliquer la règle
     * @param typeDeVerification type de vérification à appliquer pour la règle
     * @param listChaineCaracteres liste de chaines de caractères à rechercher
     */
    public PresenceChaineCaracteresWebDto(Integer id, String zone, String booleanOperator, String sousZone, String typeDeVerification, List<ChaineCaracteresWebDto> listChaineCaracteres) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.listChaineCaracteres = listChaineCaracteres;
    }

    /**
     * Constructeur avec liste de chaines de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la règle
     * @param booleanOperator opérateur logique qui définit l'enchainement des règles
     * @param sousZone sous-zone sur laquelle appliquer la règle
     * @param typeDeVerification type de vérification à appliquer pour la règle
     */
    public PresenceChaineCaracteresWebDto(Integer id, String zone, String booleanOperator, String sousZone, String typeDeVerification) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.listChaineCaracteres = new LinkedList<>();
    }


    /**
     * Méthode qui ajoute une chaine de caractères à la liste de chaines de caractères
     * @param chaineCaracteresWebDto chaine de caractères à rechercher
     */
    public void addChaineCaracteres(ChaineCaracteresWebDto chaineCaracteresWebDto) {
        this.listChaineCaracteres.add(chaineCaracteresWebDto);
    }

    /**
     * Chaine de caractères qui vient allimenter la liste de chaines de caractères de la classe PresenceChaineCaractèresWebDto
     */
    @Getter
    @NoArgsConstructor
    public static class ChaineCaracteresWebDto {
        @Pattern(regexp = "ET|OU", message = "L'opérateur doit être égal à OU ou à ET")
        @JsonProperty("operateur")
        private String operateur;

        @JsonProperty("chaine-caracteres")
        @NotNull
        private String chaineCaracteres;

        /**
         * Construction sans opérateur
         * @param chaineCaracteres chaine de caractères à rechercher
         */
        public ChaineCaracteresWebDto(String chaineCaracteres) {
            this.chaineCaracteres = chaineCaracteres;
        }

        /**
         * Constructeur avec opérateur
         * @param operateur opérateur logique pour l'enchainement des chaines de caractères
         * @param chaineCaracteres chaine de caractères à rechercher
         */
        public ChaineCaracteresWebDto(String operateur, String chaineCaracteres) {
            this.operateur = operateur;
            this.chaineCaracteres = chaineCaracteres;
        }
    }
}

/*
// SIMPLE
### Présence d'une chaine de caractères

Liste des champs propres au type de règle présence chaine caractères:
* souszone : **obligatoire** - de type caractère. La sous-zone à vérifier. ATTENTION : le $ du format Unimarc de catalogage ne doit pas être renseigné
* type-de-verification : **obligatoire** - ne peut être que `STRICTEMENT` ou `COMMENCE` ou `TERMINE` ou `CONTIENT` ou `NECONTIENTPAS`
* positionstart/positionend : **optionnel** - indexation base 0 (0 = premier caractère). Pour cibler un seul caractère : positionstart = positionend
* chaines-caracteres : **obligatoire** - de type liste d'objets. La liste des chaine-caracteres à vérifier. Les champs d'un objet de la liste sont les suivants :
    * operateur : de type opérateur logique. ne peut être que `ET` ou `OU`.
    * chaine-caracteres :  la chaine de caractères à vérifier

Liste des valeurs possibles en fonction du type de vérification :
* La vérification `STRICTEMENT` peut comporter :
    * soit une `chaine-caracteres` sans `operateur`,
    * soit une `chaine-caracteres` sans `operateur` et plusieurs `chaine-caracteres` avec `operateur` `OU`.
* La vérification `COMMENCE` peut comporter :
    * soit une `chaine-caracteres` sans `operateur`,
    * soit une `chaine-caracteres` sans `operateur` et plusieurs `chaine-caracteres` avec `operateur` `OU`,
* La vérification `TERMINE` peut comporter :
    * soit une `chaine-caracteres` sans `operateur`,
    * soit une `chaine-caracteres` sans `operateur` et plusieurs `chaine-caracteres` avec `opérateur` `OU`,
* La vérification `CONTIENT` peut comporter :
    * soit une `chaine-caracteres` sans `operateur`,
    * soit une `chaine-caracteres` sans `operateur` et plusieurs `chaine-caracteres` avec `opérateur` `ET` ou `OU`,
* La vérification `NECONTIENTPAS` peut comporter :
    * soit une `chaine-caracteres` sans `operateur`,
    * soit une `chaine-caracteres` sans `operateur` et plusieurs `chaine-caracteres` avec `opérateur` `ET` ou `OU`,

``` YAML
---
rules:
    - id:                       1
      id-excel:                 1
      type:                     presencechainecaracteres
      message:                  message de retour
      zone:                     200
      priorite:                 P1
      type-these:
          - REPRO
      souszone:                 a
      type-de-verification:     STRICTEMENT
      chaines-caracteres:
        - chaine-caracteres:    chaine de caractères à chercher
    - id:                       2
      id-excel:                 2
      type:                     presencechainecaracteres
      message:                  message de retour
      zone:                     200
      priorite:                 P1
      type-these:
          - SOUTENANCE
      souszone:                 a
      type-de-verification:     STRICTEMENT
      chaines-caracteres:
        - chaine-caracteres:    texte à chercher
        - operateur:            OU
          chaine-caracteres:    deuxième chaine de caractères à chercher
    - id:                       3
      id-excel:                 3
      type:                     presencechainecaracteres
      message:                  message de retour
      zone:                     200
      priorite:                 P1
      type-these:
          - REPRO
      souszone:                 a
      type-de-verification:     CONTIENT
      chaines-caracteres:
        - chaine-caracteres:    premier chaine de caractères à chercher
        - operateur:            ET
          chaine-caracteres:    deuxième chaine de caractères à chercher
        - operateur:            OU
          chaine-caracteres:    deuxième chaine de caractères à chercher
```
*/








