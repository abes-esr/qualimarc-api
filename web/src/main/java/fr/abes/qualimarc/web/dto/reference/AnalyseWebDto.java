package fr.abes.qualimarc.web.dto.reference;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyseWebDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("libelle")
    private String libelle;

    @JsonProperty("description")
    private String description;

    @JsonProperty("nbRules")
    private Integer nbRules;

    @JsonProperty("famillesDocument")
    private List<FamilleDocumentWebDto> famillesDocument;

    @JsonProperty("ruleSets")
    private List<RuleSetResponseWebDto> ruleSets;

    public AnalyseWebDto(String id, String libelle, String description, Integer nbRules) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.nbRules = nbRules;
    }
}
