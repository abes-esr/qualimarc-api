package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("presencezone")
@NoArgsConstructor
public class PresenceZoneWebDto extends RulesWebDto {
    @JsonProperty(value = "presence")
    private boolean isPresent;

    public PresenceZoneWebDto(Integer id, Integer idExcel, String message, String zone, Priority priority, List<String> typesDoc, boolean isPresent) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.isPresent = isPresent;
    }
}
