package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import fr.abes.qualimarc.web.dto.reference.RuleSetWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PpnWithRuleSetsRequestDto {

    @JsonProperty("ppnList")
    private List<String> ppnList;

    @JsonProperty("typeAnalyse")
    private TypeAnalyse typeAnalyse;

    @JsonProperty("famillesDocuments")
    private Set<FamilleDocumentWebDto> familleDocumentSet;

    @JsonProperty("ruleSet")
    private Set<RuleSetWebDto> ruleSet;
}