package fr.abes.qualimarc.core.model.entity.rules.rulesSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RulesSetType {

    private Boolean quickSetRule;

    private Boolean completeSetRule;

    private List<String> focusedRulesSet;

    public RulesSetType(Boolean quickSetRule, Boolean completeSetRule, List<String> focusedRulesSet){
        this.quickSetRule = quickSetRule;
        this.completeSetRule = completeSetRule;
        this.focusedRulesSet = focusedRulesSet;
    }


}
