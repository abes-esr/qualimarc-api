package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("presencezone")
public class PresenceZoneWebDto extends RulesWebDto {
    @JsonProperty(value = "presence")
    private boolean isPresent;
}
