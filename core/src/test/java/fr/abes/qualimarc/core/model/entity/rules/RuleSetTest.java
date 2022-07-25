package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {RuleSet.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class RuleSetTest {

    @Test
    void getResultQuickRulesListTest() {
        List<String> setList = new ArrayList<>();
        setList.add("quick");
        RuleSet resulQuickSet = new RuleSet(setList);
        List<Rule> ruleList = resulQuickSet.getResultRulesList();
        Assertions.assertFalse(ruleList.isEmpty());
    }

    @Test
    void getResultCompleteRuleListTest() {
        List<String> setList = new ArrayList<>();
        setList.add("complete");
        RuleSet ruleCompletedSet = new RuleSet(setList);
        List<Rule> ruleList = ruleCompletedSet.getResultRulesList();
        Assertions.assertFalse(ruleList.isEmpty());
    }

    @Test
    void getResultFocusedRuleListTest() {
        List<String> focusedSetList = new ArrayList<>();
        focusedSetList.add("focused");
        focusedSetList.add("focused01");
        RuleSet ruleFocusedSet = new RuleSet(focusedSetList);
        List<Rule> ruleList = ruleFocusedSet.getFocusedRulesList(focusedSetList);
        Assertions.assertFalse(ruleList.isEmpty());
    }

}