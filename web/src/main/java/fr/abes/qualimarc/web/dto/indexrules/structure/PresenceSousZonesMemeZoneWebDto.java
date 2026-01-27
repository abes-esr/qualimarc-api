package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@JsonTypeName("presencesouszonesmemezone")
public class PresenceSousZonesMemeZoneWebDto extends SimpleRuleWebDto {

    @JsonProperty("souszones")
    @NotEmpty
    private List<SousZoneOperatorWebDto> sousZones;

    public PresenceSousZonesMemeZoneWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String zone, String priority, List<String> typesDoc, List<String> typesThese, List<SousZoneOperatorWebDto> sousZones) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, zone, priority, typesDoc, typesThese);
        this.sousZones = sousZones;
    }

    public PresenceSousZonesMemeZoneWebDto(Integer id, String zone, String booleanOperator, List<SousZoneOperatorWebDto> sousZones) {
        super(id, zone, booleanOperator);
        this.sousZones = sousZones;
    }

    public PresenceSousZonesMemeZoneWebDto(Integer id, String zone, String booleanOperator) {
        super(id, zone, booleanOperator);
        this.sousZones = new LinkedList<>();
    }

    public PresenceSousZonesMemeZoneWebDto() {
        this.sousZones = new LinkedList<>();
    }

    public void addSousZone(SousZoneOperatorWebDto sousZoneOperatorWebDto){
        this.sousZones.add(sousZoneOperatorWebDto);
    }

    @Getter
    @NoArgsConstructor
    public static class SousZoneOperatorWebDto {

        @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
        @NotNull(message = "Le champ souszone est obligatoire")
        @JsonProperty("souszone")
        private String sousZone;

        @NotNull(message = "le champ presence est obligatoire")
        @JsonProperty("presence")
        private boolean isPresent;

        @JsonProperty("operateur-booleen")
        private BooleanOperateur operator;

        /**
         * Constructeur utilisé pour le premier souszoneoperateur
         */
        public SousZoneOperatorWebDto(String sousZone, boolean isPresent) {
            this.sousZone = sousZone;
            this.isPresent = isPresent;
        }

        public SousZoneOperatorWebDto(String sousZone, boolean isPresent, BooleanOperateur operator) {
            this.sousZone = sousZone;
            this.isPresent = isPresent;
            this.operator = operator;
        }
    }
}
/*
---
rules:
    - id:          1
      id-excel:    1
      type:        presencesouszonememezone
      message:     message test presense sous zone meme zone
      zone:        603
      priorite:    P1
      type-doc:
      -    A
      -    B
      -    K
      sous-zones:
      -    sous-zone: a
           presence: true
      -    sous-zone: b
           presence: true
           operateur: ET
      -    sous-zone: c
           presence: false
           operateur: OU
* */