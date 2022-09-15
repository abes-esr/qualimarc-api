package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("presencesouszone")
@NoArgsConstructor
public class PresenceSousZoneWebDto extends RulesWebDto {
    @JsonProperty(value = "souszone")
    private String sousZone;

    @JsonProperty(value = "presence")
    private boolean isPresent;

    public PresenceSousZoneWebDto(Integer id, Integer idExcel, String message, String zone, Priority priority, List<String> typesDoc, String sousZone, boolean isPresent) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }
}