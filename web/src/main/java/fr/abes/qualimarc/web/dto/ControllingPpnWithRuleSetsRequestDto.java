package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControllingPpnWithRuleSetsRequestDto {
    @JsonProperty("ppnList")
    private List<String> ppnList;
    @JsonProperty("typeAnalyse")
    private String typeAnalyse;
    @JsonProperty("famillesDocuments")
    private Set<FamilleDocument> famillesDocuments;
    @JsonProperty("rules")
    private Set<RuleSet> rules;

    @Override
    public String toString() {
        return "ppnList= '" + ppnList + "'," +
                "typeAnalyse= '" + typeAnalyse + "'," +
                "famillesDocuments= " + famillesDocuments + "," +
                "rules= " + rules + "";
    }
}
