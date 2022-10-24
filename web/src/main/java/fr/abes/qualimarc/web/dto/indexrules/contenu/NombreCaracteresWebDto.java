package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@JsonTypeName("nombrecaractere")
@NoArgsConstructor
public class NombreCaracteresWebDto extends SimpleRuleWebDto {
    @JsonProperty("operateur")
    @NotNull(message = "l'opérateur est obligatoire")
    private Operateur operateur;

    @JsonProperty("souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    private String sousZone;

    @JsonProperty(value = "occurrences")
    @NotNull(message = "le nombre d'occurrence est obligatoire")
    private Integer occurrences;

    public NombreCaracteresWebDto(Integer id, Integer idExcel, String message, String zone, String sousZone, String priority, List<String> typesDoc, Operateur operateur, Integer occurrences) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.sousZone = sousZone;
        this.operateur = operateur;
        this.occurrences = occurrences;
    }

    public NombreCaracteresWebDto(Integer id, String zone, String sousZone, String booleanOperator, Operateur operateur, Integer occurrences) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.operateur = operateur;
        this.occurrences = occurrences;
    }
}
