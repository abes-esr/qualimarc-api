package fr.abes.qualimarc.web.dto.indexrules.structure;

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
@JsonTypeName("positionsouszone")
public class PositionSousZoneWebDto extends SimpleRuleWebDto {
    @JsonProperty(value = "souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    private String sousZone;

    @JsonProperty(value = "position")
    @NotNull(message = "la position est obligatoire")
    private Integer position;

    public PositionSousZoneWebDto(Integer id, Integer idExcel, String message, String zone, String priority, List<String> typesDoc, String sousZone, Integer position) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.sousZone = sousZone;
        this.position = position;
    }

    public PositionSousZoneWebDto() {
        super();
    }

    public PositionSousZoneWebDto(Integer id, String zone, String booleanOperator, String sousZone, Integer position) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.position = position;
    }
}
