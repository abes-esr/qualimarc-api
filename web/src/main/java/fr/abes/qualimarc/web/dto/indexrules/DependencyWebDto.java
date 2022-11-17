package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@JsonTypeName("dependance")
public class DependencyWebDto extends SimpleRuleWebDto {
    @JsonProperty(value = "souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    private String sousZone;

    @JsonProperty(value = "type-notice-liee")
    @NotNull(message = "Le champ type-notice-liee est obligatoire")
    private String typeNoticeLiee;

    public DependencyWebDto() {
        super();
    }

    public DependencyWebDto(Integer id, String zone, String sousZone, String typeNoticeLiee) {
        super(id, zone);
        this.sousZone = sousZone;
        this.typeNoticeLiee = typeNoticeLiee;
    }
}
