package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonTypeName("nombresouszone")
public class NombreSousZoneWebDto extends SimpleRuleWebDto {

    @JsonProperty(value = "souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    private String sousZone;

    @JsonProperty(value = "zonecible")
    @Pattern(regexp = "\\b([A-Z]{0,1}[0-9]{3})\\b", message = "Le champ zone peut contenir : soit trois chiffres, soit une lettre majuscule suivie de trois chiffres.")
    @NotNull(message = "Le champ zonecible est obligatoire")
    private String zoneCible;

    @JsonProperty(value = "souszonecible")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszonecible est obligatoire")
    private String sousZoneCible;

    public NombreSousZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, String zoneCible, String sousZoneCible) {
        super(id, idExcel, ruleSetList, message, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    public NombreSousZoneWebDto() {
        super();
    }

    public NombreSousZoneWebDto(Integer id, String zone, String booleanOperator, String sousZone, String zoneCible, String sousZoneCible) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }
}
