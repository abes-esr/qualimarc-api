package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PresenceZoneWebDto.class, name = "presencezone")
})
public abstract class RulesWebDto {
    @JsonProperty(value = "id")
    protected Integer id;
    @JsonProperty(value = "id-excel")
    protected Integer idExcel;
    @JsonProperty(value = "message")
    protected String message;
    @JsonProperty(value = "zone")
    protected String zone;
    @JsonProperty(value = "priority")
    protected Priority priority;
    @JsonProperty(value = "type-doc")
    protected List<String> typesDoc;

}
