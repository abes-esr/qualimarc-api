package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PresenceZoneWebDto.class, name = "presencezone"),
        @JsonSubTypes.Type(value = PresenceSousZoneWebDto.class, name = "presencesouszone"),
        @JsonSubTypes.Type(value = NombreZoneWebDto.class, name = "nombrezone"),
        @JsonSubTypes.Type(value = NombreSousZoneWebDto.class, name = "nombresouszone")
})
public abstract class RulesWebDto {
    @JsonProperty(value = "id")
    @NotNull(message = "Le champ id est obligatoire")
    @NotBlank(message = "Le champ id est obligatoire")
    protected Integer id;

    @JsonProperty(value = "id-excel")
    protected Integer idExcel;

    @JsonProperty(value = "message")
    @NotNull(message = "Le champ message est obligatoire")
    @NotBlank(message = "Le champ message est obligatoire")
    protected String message;

    @JsonProperty(value = "zone")
    @NotNull(message = "Le champ zone est obligatoire")
    @NotBlank(message = "Le champ zone est obligatoire")
    protected String zone;

    @JsonProperty(value = "priorite")
    @NotNull(message = "Le champ priorite est obligatoire")
    @NotBlank(message = "Le champ priorite est obligatoire")
    protected Priority priority;

    @JsonProperty(value = "type-doc")
    protected List<String> typesDoc;

    public RulesWebDto(Integer id, Integer idExcel, String message, String zone, Priority priority, List<String> typesDoc) {
        this.id = id;
        this.idExcel = idExcel;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.typesDoc = typesDoc;
    }
}
