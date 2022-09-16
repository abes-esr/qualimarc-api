package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonTypeName("nombrezone")
@NoArgsConstructor
public class NombreZoneWebDto extends RulesWebDto {

    @JsonProperty(value = "operateur")
    @NotNull(message = "l'opérateur est obligatoire")
    private Operateur operateur;

    @JsonProperty(value = "occurences")
    @NotNull(message = "le nombre d'occurrence est obligatoire")
    private Integer occurrences;

    public NombreZoneWebDto(Integer id, Integer idExcel, String message, String zone, Priority priority, List<String> typesDoc, Operateur operateur, Integer occurrences) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.operateur = operateur;
        this.occurrences = occurrences;
    }
}
