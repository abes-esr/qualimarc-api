package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonCreator;
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
public class PresenceZoneWebDto extends SimpleRuleWebDto {
    @NotNull(message = "le champ presence est obligatoire")
    private boolean isPresent;

    public boolean isPresent() {
        return isPresent;
    }

    @JsonCreator
    public PresenceZoneWebDto(@JsonProperty("id") Integer id,
                              @JsonProperty("id-excel") Integer idExcel,
                              @JsonProperty("message") String message,
                              @JsonProperty("zone") String zone,
                              @JsonProperty("priorite") String priority,
                              @JsonProperty("type-doc") List<String> typesDoc,
                              @JsonProperty("presence") boolean isPresent) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.isPresent = isPresent;
    }

    public PresenceZoneWebDto() {
        super();
    }
}
