package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("comparaisondate")
public class ComparaisonDateWebDto extends SimpleRuleWebDto {
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    @JsonProperty("souszone")
    private String sousZone;

    @JsonProperty("positionstart")
    private Integer positionStart;

    @JsonProperty("positionend")
    private Integer positionEnd;

    @Pattern(regexp = "\\b([A-Z]{0,1}[0-9]{3}|4XX|5XX|6XX|7XX)\\b", message = "Le champ zonecible doit contenir : soit trois chiffres, soit une lettre majuscule suivie de trois chiffres, soit une zone générique (4XX, 5XX, 6XX, ou 7XX).")
    @NotNull(message = "Le champ zonecible est obligatoire")
    @NotBlank(message = "Le champ zonecible est obligatoire")
    @JsonProperty("zonecible")
    private String zoneCible;

    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszonecible ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszonecible est obligatoire")
    @JsonProperty("souszonecible")
    private String sousZoneCible;

    @JsonProperty("positionstartcible")
    private Integer positionStartCible;

    @JsonProperty("positionendcible")
    private Integer positionEndCible;

    @NotNull(message = "Le champ operateur est obligatoire")
    @JsonProperty("comparateur")
    private String comparateur;

    public ComparaisonDateWebDto(Integer id, String booleanOperator, String zone, String sousZone, Integer positionStart, Integer positionEnd, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible, String comparateur) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.comparateur = comparateur;
    }

    public ComparaisonDateWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, Integer positionStart, Integer positionEnd, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible, String comparateur) {
        super(id, idExcel, ruleSetList, message, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.comparateur = comparateur;
    }
}

/*
 * Exemple SIMPLE YAML
 * ---
 * rules:
 *    - id: 1
 *      idExcel: 1
 *      jeuxDeRegles:
 *          - 1
 *          - 2
 *      message: "La date de publication doit être antérieure à la date de soutenance."
 *      zone: "260"
 *      sousZone: "c"
 *      positionStart: 7
 *      positionEnd: 16
 *      zoneCible: "269"
 *      sousZoneCible: "c"
 *      positionStartCible: 7
 *      positionEndCible: 16
 *      operateur-booleen: "SUPERIEUR"
 *      priorite: "1"
 *
 * Exemple COMPLEXE YAML
 * ---
 * rules:
 *    - id: 1
 *      idExcel: 1
 *      jeuxDeRegles:
 *          - 1
 *          - 2
 *      message: "La date de publication doit être antérieure à la date de soutenance."
 *      regles:
 *        - id: 1
 *          zone: "260"
 *          sousZone: "c"
 *          positionStart: 7
 *          positionEnd: 16
 *          zoneCible: "269"
 *          sousZoneCible: "c"
 *          positionStartCible: 7
 *          positionEndCible: 16
 *          comparateur: "SUPERIEUR"
 *        - id: 2
 *          zone: "260"
 *          sousZone: "c"
 *          positionStart: 7
 *          positionEnd: 16
 *          zoneCible: "269"
 *          sousZoneCible: "c"
 *          positionStartCible: 7
 *          positionEndCible: 16
 *          comparateur: "INFERIEUR"
 *          operateur-booleen: "ET"
 *          priorite: "1"
 *
 */
