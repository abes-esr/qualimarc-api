package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonTypeName("nombrezone")
@NoArgsConstructor
public class NombreZoneWebDto extends SimpleRuleWebDto {

    @JsonProperty(value = "operateur")
    @NotNull(message = "l'opérateur est obligatoire")
    private Operateur operateur;

    @JsonProperty(value = "occurrences")
    @NotNull(message = "le nombre d'occurrence est obligatoire")
    private Integer occurrences;

    public NombreZoneWebDto(Integer id, Integer idExcel, String message, String zone, String priority, List<String> typesDoc, Operateur operateur, Integer occurrences) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.operateur = operateur;
        this.occurrences = occurrences;
    }
}
