package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
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

    private List<String> ruleSetList;

    public RuleSet(List<String> ruleSetList){
        this.ruleSetList = ruleSetList;
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
     * Gets the list of complete rules
     * @return ruleList
     */
    public List<Rule> getCompleteRulesList(){
        List<Rule> ruleList = new ArrayList<>();
        //  First rule of the advanced rule set
        ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
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
            if (ruleSet.equals("focused01")) {
                //  First rule of the focused rule set
                ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
            }
            if (ruleSet.equals("focused02")) {
                //  Second rule of the focused rule set
                ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
            }
            if (ruleSet.equals("focused03")) {
                //  Third rule of the focused rule set
                ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
            }
        }
        return ruleList;
    }

    /**
     * Method that detects the requested rule set and calls the associated methods
     * @return ruleList
     */
    public List<Rule> getResultRulesList() {
        List<Rule> ruleList = new ArrayList<>();
        //  Checks if a rule set appears more than once and if so throws an exception.
        //  If not, calls the methods that get the list of rules corresponding to the requested ruleset
        if(this.ruleSetList.stream().filter(i -> i.equals("quick")).count() == 1){
            //  Calls up the quick rule handling methods
            ruleList.addAll(getQuickRulesList());
        } else if (this.ruleSetList.stream().filter(i -> i.equals("complete")).count() == 1) {
            //  Calls up the quick rule handling methods
            ruleList.addAll(getQuickRulesList());
            //  Calls up the advanced rule handling methods
            ruleList.addAll(getCompleteRulesList());
        } else if (this.ruleSetList.stream().filter(i -> i.equals("focused")).count() == 1) {
            //  Calls up the focused rules handling methods
            ruleList.addAll(getFocusedRulesList(this.ruleSetList));
        } else throw new IllegalRulesSetException("Type de jeu de règles inconnu");
        return ruleList;
    }
}