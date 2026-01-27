package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("typecaractere")
public class TypeCaractereWebDto extends SimpleRuleWebDto {
    @JsonProperty(value = "souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "La sous zone est obligatoire")
    @NotBlank(message = "la sous zone ne peut pas être vide")
    private String sousZone;

    @JsonProperty("type-caracteres")
    private List<String> typeCaracteres;

    public TypeCaractereWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, List<String> typeCaracteres) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.typeCaracteres = typeCaracteres;

    }

    public TypeCaractereWebDto(Integer id, String zone, String booleanOperator, String sousZone, List<String> typeCaracteres) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.typeCaracteres = typeCaracteres;
    }

    public TypeCaractereWebDto() {super();}
}
