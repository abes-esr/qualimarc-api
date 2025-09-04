package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonTypeName("presencezone")
@NoArgsConstructor
public class PresenceZoneWebDto extends SimpleRuleWebDto {
    @NotNull(message = "le champ presence est obligatoire")
    @JsonProperty("presence")
    private boolean isPresent;

    public boolean isPresent() {
        return isPresent;
    }


    public PresenceZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, boolean isPresent) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.isPresent = isPresent;
    }

    public PresenceZoneWebDto(Integer id, String zone, String operateur, boolean isPresent){
        super(id,zone,operateur);
        this.isPresent = isPresent;
    }

    public PresenceZoneWebDto(Integer id, String zone, boolean isPresent) {
        super(id, zone);
        this.isPresent = isPresent;
    }
}
