package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.List;

@Data
@JsonRootName("rules")
public class ListComplexRulesWebDto {
    private List<ComplexRuleWebDto> complexRules;

    public void addComplexRule(ComplexRuleWebDto rule) {
        this.complexRules.add(rule);
    }
}
