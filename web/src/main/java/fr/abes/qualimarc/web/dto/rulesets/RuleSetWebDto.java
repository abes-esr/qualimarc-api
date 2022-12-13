package fr.abes.qualimarc.web.dto.rulesets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RuleSetWebDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("libelle")
    private String libelle;

    @JsonProperty("description")
    private String description;

    @JsonProperty("position")
    private Integer position;

    @JsonCreator
    public RuleSetWebDto(@JsonProperty("id") Integer id,
                         @JsonProperty("libelle") String libelle,
                         @JsonProperty("description") String description,
                         @JsonProperty("position") Integer position) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.position = position;
    }
}
