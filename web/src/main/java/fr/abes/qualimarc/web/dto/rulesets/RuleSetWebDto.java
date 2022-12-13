package fr.abes.qualimarc.web.dto.rulesets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleSetWebDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("libelle")
    private String libelle;

    @JsonProperty("description")
    private String description;

    @JsonProperty("position")
    private Integer position;
}
