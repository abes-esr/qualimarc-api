package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.*;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PresenceZoneWebDto.class, name = "presencezone"),
        @JsonSubTypes.Type(value = PresenceSousZoneWebDto.class, name = "presencesouszone"),
        @JsonSubTypes.Type(value = NombreZoneWebDto.class, name = "nombrezone"),
        @JsonSubTypes.Type(value = NombreSousZoneWebDto.class, name = "nombresouszone"),
        @JsonSubTypes.Type(value = PositionSousZoneWebDto.class, name="positionsouszone")
})
public abstract class SimpleRuleWebDto {
    @NotNull(message = "Le champ id est obligatoire")
    protected Integer id;

    protected Integer idExcel;

    @NotNull(message = "Le champ message est obligatoire")
    @NotBlank(message = "Le champ message est obligatoire")
    protected String message;

    @Pattern(regexp = "([P]{1}[1-2]{1}){1}", message = "Le champ priorité ne peut contenir qu'une des deux valeurs : P1 ou P2")
    @NotNull(message = "Le champ priorite est obligatoire")
    @NotBlank(message = "Le champ priorite est obligatoire")
    protected String priority;

    protected List<@Pattern(regexp = "\\b([A-Z]{0,2}){0,}\\b", message = "Le champ message ne peut contenir qu'une ou deux lettre(s) majuscule(s).") String> typesDoc = new ArrayList<>();


    @Pattern(regexp = "\\b([A-Z]{0,1}[0-9]{3})\\b", message = "Le champ zone doit contenir : soit trois chiffres, soit une lettre majuscule suivie de trois chiffres.")
    @NotNull(message = "Le champ zone est obligatoire")
    @NotBlank(message = "Le champ zone est obligatoire")
    protected String zone;

    @JsonCreator
    public SimpleRuleWebDto(@JsonProperty("id") Integer id,
                            @JsonProperty("id-excel") Integer idExcel,
                            @JsonProperty("message") String message,
                            @JsonProperty("zone") String zone,
                            @JsonProperty("priorite") String priority,
                            @JsonProperty("type-doc") List<String> typesDoc) {
        this.id = id;
        this.idExcel = idExcel;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.typesDoc = typesDoc;
    }

    public SimpleRuleWebDto() {
    }
}
