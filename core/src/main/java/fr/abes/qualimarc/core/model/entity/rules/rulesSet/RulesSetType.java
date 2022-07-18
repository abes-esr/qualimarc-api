package fr.abes.qualimarc.core.model.entity.rules.rulesSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RulesSetType {

    private Boolean quickSetRule;

    private Boolean completeSetRule;

    private FocusedRulesSet focusedRulesSet;

    public RulesSetType(Boolean quickSetRule, Boolean completeSetRule, FocusedRulesSet focusedRulesSet){
        this.quickSetRule = quickSetRule;
        this.completeSetRule = completeSetRule;
        this.focusedRulesSet = focusedRulesSet;
    }


}
