package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.model.entity.rules.rulesSet.FocusedRulesSet;
import fr.abes.qualimarc.core.model.entity.rules.rulesSet.RulesSetType;
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

    private String typeRuleSet;

    private List<String> typeRuleSetList;

    public RuleSet(String typeRuleSet){
        this.typeRuleSet = typeRuleSet;
    }

    public RuleSet(List<String> typeRuleSetList){
        this.typeRuleSetList = typeRuleSetList;
    }

    /**
     * Gets the list of quick rules
     * @return ruleList
     */
    private List<Rule> getQuickRulesList(){
        List<Rule> ruleList = new ArrayList<>();
        //  Première règle du jeu de règles basique
        ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
        return ruleList;
    }

    /**
     * Gets the list of advanced rules
     * @return ruleList
     */
    private List<Rule> getAdvancedRulesList(){
        List<Rule> ruleList = new ArrayList<>();
        //  Première règle du jeu de règles avancé
        ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
        return ruleList;
    }

    /**
     * Gets the list of targeted rules
     * @param focusedRulesSet FocusedRulesSet
     * @return ruleList
     */
    private List<Rule> getFocusedRulesList(FocusedRulesSet focusedRulesSet){
        List<Rule> ruleList = new ArrayList<>();
        if (focusedRulesSet.getFocusedRulesSet01()) {
            //  Première règle du jeu de règles ciblées
            ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
        } else if (focusedRulesSet.getFocusedRulesSet02()) {
            //  Deuxième règle du jeu de règles ciblées
            ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
        } else if (focusedRulesSet.getFocusedRulesSet03()) {
            //  Troisième règle du jeu de règles ciblées
            ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", true));
        }
        return ruleList;
    }

    /**
     * Executes the rules determined by the ruleSetList
     * @param rulesSetType RulesSetType
     * @return resultRuleList
     */
    public List<Rule> getRuleList(RulesSetType rulesSetType) {
        List<Rule> resultRuleList = new ArrayList<>();
        if(rulesSetType.getQuickSetRule()){
            //  Calls up the quick rule handling methods
            List<Rule> tempListBasic = new ArrayList<>(getQuickRulesList());
            for (Rule rule : tempListBasic
            ) {
                resultRuleList.add(rule);
            }
        } else if(rulesSetType.getCompleteSetRule()) {
            //  Calls up the quick rule handling methods
            List<Rule> tempListBasic = new ArrayList<>(getQuickRulesList());
            for (Rule rule : tempListBasic
            ) {
                resultRuleList.add(rule);
            }
            //  Calls up the advanced rule handling methods
            List<Rule> tempListAdvanced = new ArrayList<>(getAdvancedRulesList());
            for (Rule rule : tempListAdvanced
            ) {
                resultRuleList.add(rule);
            }
        } else if (rulesSetType.getFocusedRulesSet() != null) {
            //  Calls up the focused rules handling methods     //  rulesSetType.getFocusedRulesSet().getFocusedRulesSetOn()
            List<Rule> temp = new ArrayList<>(getFocusedRulesList(rulesSetType.getFocusedRulesSet()));
            for (Rule rule : temp
                 ) {
                resultRuleList.add(rule);
            }
        } else {
            throw new IllegalRulesSetException("Type de jeu de règles inconnu");
        }
        return resultRuleList;
    }
}
