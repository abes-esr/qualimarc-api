package fr.abes.qualimarc.web.dto;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PpnWithRuleSetsRequestDto {

    private List<String> ppnList;

    private TypeAnalyse typeAnalyse;

    private Set<FamilleDocument> familleDocumentSet;

    private Set<RuleSet> ruleSet;

    public PpnWithRuleSetsRequestDto(List<String> ppnList, TypeAnalyse typeAnalyse, Set<FamilleDocument> familleDocumentSet, Set<RuleSet> ruleSet) {
        this.ppnList = ppnList;
        this.typeAnalyse = typeAnalyse;
        this.familleDocumentSet = familleDocumentSet;
        this.ruleSet= ruleSet;
    }
}
