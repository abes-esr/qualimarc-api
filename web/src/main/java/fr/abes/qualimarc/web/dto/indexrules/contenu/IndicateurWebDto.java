package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("indicateur")
public class IndicateurWebDto extends SimpleRuleWebDto {

    @NotNull(message = "le champ indicateur est obligatoire")
    @JsonProperty("indicateur")
    private Integer indicateur;

    @NotNull(message = "le champ valeur est obligatoire")
    @Pattern(regexp = "([0-9|#])", message = "le champ valeur peut avoir une valeur de 0 à 9 ou un '#'")
    @JsonProperty("valeur")
    private String valeur;

    @Pattern(regexp = "STRICTEMENT|STRICTEMENTDIFFERENT", message = "Le champ niveau de verification ne peut contenir que STRICTEMENT ou STRICTEMENTDIFFERENT")
    @JsonProperty("type-de-verification")
    @NotNull
    private String typeDeVerification;

    public IndicateurWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, Integer indicateur, String valeur, String typeDeVerification) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.indicateur = indicateur;
        this.valeur = valeur;
        this.typeDeVerification = typeDeVerification;
    }

    public IndicateurWebDto(Integer id, String zone, String booleanOperator, Integer indicateur, String valeur, String typeDeVerification) {
        super(id, zone, booleanOperator);
        this.indicateur = indicateur;
        this.valeur = valeur;
        this.typeDeVerification = typeDeVerification;
    }

    public IndicateurWebDto() {
        super();
    }
}
