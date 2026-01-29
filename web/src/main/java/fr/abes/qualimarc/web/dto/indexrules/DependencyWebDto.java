package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("dependance")
@NoArgsConstructor
public class DependencyWebDto extends SimpleRuleWebDto {
    @JsonProperty(value = "souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    private String sousZone;

    @JsonProperty(value = "type-notice-liee")
    @NotNull(message = "Le champ type-notice-liee est obligatoire")
    private String typeNoticeLiee;

    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ position ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("position")
    private String position;


    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionStart ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionStart")
    private String positionStart;


    @Pattern(regexp = "(\\b([0-9]{0,3})\\b)", message = "le champ positionEnd ne peut contenir que 3 chiffres au maximum.")
    @JsonProperty("positionEnd")
    private String positionEnd;

    public DependencyWebDto(Integer id, String zone, String sousZone, String typeNoticeLiee, String position, String positionStart, String positionEnd) {
        super(id, zone);
        this.sousZone = sousZone;
        this.typeNoticeLiee = typeNoticeLiee;
        this.position = position;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
    }
}
