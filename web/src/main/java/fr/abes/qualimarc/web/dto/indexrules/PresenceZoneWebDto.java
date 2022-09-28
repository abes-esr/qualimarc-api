package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonTypeName("presencezone")
@NoArgsConstructor
public class PresenceZoneWebDto extends RulesWebDto {
    @JsonProperty(value = "presence")
    @NotNull(message = "le champ presence est obligatoire")
    private boolean isPresent;

    public boolean isPresent() {
        return isPresent;
    }

    public PresenceZoneWebDto(Integer id, Integer idExcel, String message, String zone, String priority, List<String> typesDoc, boolean isPresent) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.isPresent = isPresent;
    }
}
