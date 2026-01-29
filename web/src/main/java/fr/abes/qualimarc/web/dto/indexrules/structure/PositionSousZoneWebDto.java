package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@JsonTypeName("positionsouszone")
public class PositionSousZoneWebDto extends SimpleRuleWebDto {
    @JsonProperty(value = "souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    private String sousZone;

    @JsonProperty(value = "positions")
    @NotNull(message = "Au moins une position est obligatoire")
    private List<PositionsOperatorWebDto> positions;

    @JsonProperty(value = "operateur")
    @Pattern(regexp = "ET|OU", message = "L'opérateur doit être égal à OU ou à ET")
    @NotNull(message = "L'opérateur est obligatoire")
    private BooleanOperateur booleanOperateur;

    public PositionSousZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, String sousZone, List<PositionsOperatorWebDto> positions, BooleanOperateur booleanOperateur) {
        super(id, idExcel, ruleSetList,  message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZone = sousZone;
        this.positions = positions;
        this.booleanOperateur = booleanOperateur;
    }

    public PositionSousZoneWebDto(Integer id, String zone, String booleanOperator, String sousZone, List<PositionsOperatorWebDto> positions, BooleanOperateur booleanOperateur) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.positions = positions;
        this.booleanOperateur = booleanOperateur;
    }

    public PositionSousZoneWebDto() {this.positions = new LinkedList<>();}

    @Getter
    @NoArgsConstructor
    public static class PositionsOperatorWebDto {
        @JsonProperty(value = "position")
        private Integer position;
        @Pattern(regexp = "EGAL|DIFFERENT|INFERIEUR|SUPERIEUR|INFERIEUR_EGAL|SUPERIEUR_EGAL", message = "Le champ comparateur ne peut contenir que EGAL, DIFFERENT, INFERIEUR, SUPERIEUR, INFERIEUR_EGAL, SUPERIEUR_EGAL")
        @JsonProperty(value = "comparateur")
        private ComparaisonOperateur comparateur;

        public PositionsOperatorWebDto(Integer position, ComparaisonOperateur comparateur) {
            this.position = position;
            this.comparateur = comparateur;
        }
    }
}
