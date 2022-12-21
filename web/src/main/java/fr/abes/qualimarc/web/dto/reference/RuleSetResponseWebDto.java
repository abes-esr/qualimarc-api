package fr.abes.qualimarc.web.dto.reference;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RuleSetResponseWebDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("libelle")
    private String libelle;

    @JsonProperty("description")
    private String description;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("nbRules")
    private Integer nbRules;

    public RuleSetResponseWebDto(Integer id, String libelle, String description, Integer position) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.position = position;
    }

    public RuleSetResponseWebDto(Integer id, String libelle, String description, Integer position, Integer nbRules) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.position = position;
        this.nbRules = nbRules;
    }
}
