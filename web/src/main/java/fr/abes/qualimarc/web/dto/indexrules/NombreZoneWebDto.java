package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeName("nombrezone")
@NoArgsConstructor
public class NombreZoneWebDto extends RulesWebDto {

    @JsonProperty(value = "operateur")
    private Operateur operateur;

    @JsonProperty(value = "occurences")
    private Integer occurences;

    public NombreZoneWebDto(Integer id, Integer idExcel, String message, String zone, Priority priority, List<String> typesDoc, Operateur operateur, Integer occurences) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.operateur = operateur;
        this.occurences = occurences;
    }
}
