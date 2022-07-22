package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.model.entity.rules.structure.PresenceZone;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RuleSet {

    private String ruleSetName;

    private List<String> ruleSetNameList;

    public RuleSet(String ruleSetName){
        this.ruleSetName = ruleSetName;
    }

    public RuleSet(List<String> ruleSetNameList){
        this.ruleSetNameList = ruleSetNameList;
    }

    /**
     * Gets the list of quick rules
     * @return ruleList
     */
    public List<Rule> getQuickRulesList(){
        List<Rule> ruleList = new ArrayList<>();
        //  First rule of the quick rule set
        ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
        return ruleList;
    }

    /**
     * Gets the list of advanced rules
     * @return ruleList
     */
    public List<Rule> getAdvancedRulesList(){
        List<Rule> ruleList = new ArrayList<>();
        //  First rule of the advanced rule set
        ruleList.add(new PresenceZone(1, "La zone 020 doit être présente", "020", true));
        return ruleList;
    }

    /**
     * Gets the list of targeted rules
     * @param focusedRulesSet FocusedRulesSet
     * @return ruleList
     */
    public List<Rule> getFocusedRulesList(List<String> focusedRulesSet){
        List<Rule> ruleList = new ArrayList<>();

        for (String ruleSet : focusedRulesSet
        ) {
            if (ruleSet.equals("P4")) {
                //  First rule of the focused rule set
                ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
            }
            if (ruleSet.equals("P5")) {
                //  Second rule of the focused rule set
                ruleList.add(new PresenceZone(1, "La zone 020 doit être présente", "020", true));
            }
            if (ruleSet.equals("P6")) {
                //  Third rule of the focused rule set
                ruleList.add(new PresenceZone(1, "La zone 030 doit être présente", "030", true));
            }
        }

        return ruleList;
    }
}