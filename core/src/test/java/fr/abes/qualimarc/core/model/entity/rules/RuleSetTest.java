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
    void getResultQuickRulesSet() {
        RuleSet ruleSet1 = new RuleSet();
        List<Rule> ruleList = ruleSet1.getQuickRulesList();
        Assertions.assertFalse(ruleList.isEmpty());
    }

    @Test
    void getResultCompleteRuleTest() {
        RuleSet ruleSet2 = new RuleSet();
        List<Rule> ruleList = ruleSet2.getAdvancedRulesList();
        Assertions.assertFalse(ruleList.isEmpty());
    }

    @Test
    void getResultFocusedRuleTest() {
        RuleSet ruleSet3 = new RuleSet();
        List<String> focusedSetList = new ArrayList<>();
        focusedSetList.add("P4");
        List<Rule> ruleList = ruleSet3.getFocusedRulesList(focusedSetList);
        Assertions.assertFalse(ruleList.isEmpty());
    }
}