package fr.abes.qualimarc.core.model.entity.rules.rulesSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FocusedRulesSet {

    private Boolean focusedRulesSet01;

    private Boolean focusedRulesSet02;

    private Boolean focusedRulesSet03;

    public FocusedRulesSet(Boolean focused01, Boolean focusedRulesSet02, Boolean focusedRulesSet03){
        this.focusedRulesSet01 = focused01;
        this.focusedRulesSet02 = focusedRulesSet02;
        this.focusedRulesSet03 = focusedRulesSet03;
    }

}
