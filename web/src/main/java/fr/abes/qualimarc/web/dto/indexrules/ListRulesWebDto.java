package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ListRulesWebDto {
    @JsonProperty(value = "rules")
    @NotNull(message = "le champ rules est obligatoire")
    private List<RulesWebDto> listRulesWebDto; 
}
