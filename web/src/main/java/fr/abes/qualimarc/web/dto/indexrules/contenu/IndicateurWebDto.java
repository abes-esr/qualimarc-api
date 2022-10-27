package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    public IndicateurWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, String zone, String priority, List<String> typesDoc, List<String> typesThese, Integer indicateur, String valeur) {
        super(id, idExcel, ruleSetList, message, zone, priority, typesDoc, typesThese);
        this.indicateur = indicateur;
        this.valeur = valeur;
    }

    public IndicateurWebDto(Integer id, String zone, String booleanOperator, Integer indicateur, String valeur) {
        super(id, zone, booleanOperator);
        this.indicateur = indicateur;
        this.valeur = valeur;
    }

    public IndicateurWebDto() {
        super();
    }
}
