package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonTypeName("nombrezone")
public class NombreZoneWebDto extends SimpleRuleWebDto {

    @JsonProperty(value = "operateur")
    @NotNull(message = "l'opérateur est obligatoire")
    private ComparaisonOperateur comparaisonOperateur;

    @JsonProperty(value = "occurrences")
    @NotNull(message = "le nombre d'occurrence est obligatoire")
    private Integer occurrences;

    public NombreZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, String zone, String priority, List<String> typesDoc, List<String> typesThese, ComparaisonOperateur comparaisonOperateur, Integer occurrences) {
        super(id, idExcel, ruleSetList,  message, zone, priority, typesDoc, typesThese);
        this.comparaisonOperateur = comparaisonOperateur;
        this.occurrences = occurrences;
    }

    public NombreZoneWebDto() {
        super();
    }

    public NombreZoneWebDto(Integer id, String zone, String booleanOperator, ComparaisonOperateur comparaisonOperateur, Integer occurrences) {
        super(id, zone, booleanOperator);
        this.comparaisonOperateur = comparaisonOperateur;
        this.occurrences = occurrences;
    }
}
