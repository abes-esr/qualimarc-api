package fr.abes.qualimarc.web.dto.indexrules.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("groupememezone")
public class SameZoneRuleGroupWebDto extends SimpleRuleWebDto {
    @JsonProperty("regles")
    @NotNull(message = "Le groupe meme zone doit contenir au moins une regle")
    @NotEmpty(message = "Le groupe meme zone doit contenir au moins une regle")
    private List<SimpleRuleWebDto> regles = new LinkedList<>();

    public SameZoneRuleGroupWebDto(Integer id, String zone, String booleanOperator, List<SimpleRuleWebDto> regles) {
        super(id, zone, booleanOperator);
        this.regles = regles;
    }
}
