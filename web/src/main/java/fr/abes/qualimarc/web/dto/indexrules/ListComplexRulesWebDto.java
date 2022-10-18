package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.List;

@Data
public class ListComplexRulesWebDto {
    @JsonProperty("rules")
    private List<ComplexRuleWebDto> complexRules;

    public void addComplexRule(ComplexRuleWebDto rule) {
        this.complexRules.add(rule);
    }
}
