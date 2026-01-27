package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Classe WebDto qui définie une règle permettant de tester la présence d'une zone et sous-zone ainsi
 * que la comparaison entre la position ou la conformité d'une ou plusieurs chaine de caractères
 * de deux sous-zone
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("comparaisoncontenusouszone")
public class ComparaisonContenuSousZoneWebDto extends SimpleRuleWebDto {

    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    @JsonProperty("souszone")
    private String sousZone;

    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionstart ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionstart")
    private String positionStart;

    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionend ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionend")
    private String positionEnd;


    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ position ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("position")
    private String position;

    @Pattern(regexp = "STRICTEMENT|CONTIENT|COMMENCE|TERMINE|NECONTIENTPAS|STRICTEMENTDIFFERENT", message = "Le champ niveau de verification ne peut contenir que STRICTEMENT, CONTIENT, COMMENCE, TERMINE, NECONTIENTPAS, STRICTEMENTDIFFERENT")
    @JsonProperty("type-de-verification")
    @NotNull
    private String typeVerification;

    @Pattern(regexp = "(\\b([0-9]{0,2})\\b)", message = "Le champ nombreCaracteres ne peut contenir que des chiffres.")
    @JsonProperty("nombreCaracteres")
    private String nombreCaracteres;

    @Pattern(regexp = "\\b([0-9]{3})\\b", message = "Le champ zonecible doit contenir : soit trois chiffres, soit une lettre majuscule suivie de trois chiffres.")
    @NotNull(message = "Le champ zonecible est obligatoire")
    @NotBlank(message = "Le champ zonecible est obligatoire")
    @JsonProperty("zonecible")
    private String zoneCible;

    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszonecible ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszonecible est obligatoire")
    @JsonProperty("souszonecible")
    private String sousZoneCible;


    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionstartcible ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionstartcible")
    private String positionStartCible;


    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionendcible ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionendcible")
    private String positionEndCible;


    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positioncible ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positioncible")
    private String positionCible;

    /**
     *
     * @param id identifiant de la règle
     * @param idExcel identifiant excel de la règle
     * @param ruleSetList list de règles
     * @param message message
     * @param zone zone sur laquelle appliquer la règle
     * @param priority priorité de la règle
     * @param typesDoc type de document de la notice sur laquelle appliquer la règle
     * @param typesThese type de thèse de la notice sur laquelle appliquer la règle
     * @param sousZone sous-zone sur laquelle appliquer la règle
     * @param zoneCible zone cible pour la vérification
     * @param sousZoneCible sous-zone cible pour la vérification
     * @param typeVerification type de vérification à appliquer à la règle
     */
    public ComparaisonContenuSousZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String typeVerification, String zoneCible, String sousZoneCible) {
        super(id, idExcel, ruleSetList, message,affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.typeVerification = typeVerification;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    /**
     *
     * @param id identifiant de la règle
     * @param idExcel identifiant excel de la règle
     * @param ruleSetList list de règles
     * @param message message
     * @param zone zone sur laquelle appliquer la règle
     * @param priority priorité de la règle
     * @param typesDoc type de document de la notice sur laquelle appliquer la règle
     * @param typesThese type de thèse de la notice sur laquelle appliquer la règle
     * @param sousZone sous-zone sur laquelle appliquer la règle
     * @param zoneCible zone cible pour la vérification
     * @param sousZoneCible sous-zone cible pour la vérification
     * @param typeVerification type de vérification à appliquer à la règle
     */
    public ComparaisonContenuSousZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String typeVerification, String nombreCaracteres, String zoneCible, String sousZoneCible) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.typeVerification = typeVerification;
        this.nombreCaracteres = nombreCaracteres;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    public ComparaisonContenuSousZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String positionStart, String positionEnd, String typeVerification, String nombreCaracteres, String zoneCible, String sousZoneCible, String positionStartCible, String positionEndCible) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.typeVerification = typeVerification;
        this.nombreCaracteres = nombreCaracteres;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
    }


    public ComparaisonContenuSousZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String position, String typeVerification, String nombreCaracteres, String zoneCible, String sousZoneCible, String positionCible) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.position = position;
        this.typeVerification = typeVerification;
        this.nombreCaracteres = nombreCaracteres;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionCible = positionCible;
    }

    public ComparaisonContenuSousZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String positionStart, String positionEnd, String position, String typeVerification, String nombreCaracteres, String zoneCible, String sousZoneCible, String positionStartCible, String positionEndCible, String positionCible) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.position = position;
        this.typeVerification = typeVerification;
        this.nombreCaracteres = nombreCaracteres;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.positionCible = positionCible;
    }
}
