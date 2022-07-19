package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.rules.rulesSet.RulesSetType;
import fr.abes.qualimarc.core.model.entity.rules.structure.PresenceZone;
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
        PresenceZone rule = new PresenceZone(1, "La zone 010 doit être présente", "010", true);
        List<Rule> ruleListControl = new ArrayList<>();
        ruleListControl.add(rule);

        //  Test du jeu de règles rapide
        List<String> focusedRulesSet1 = new ArrayList<>();
        RulesSetType rulesSetType1 = new RulesSetType(true, false, focusedRulesSet1);
        RuleSet ruleSet1 = new RuleSet();
        List<Rule> ruleList = ruleSet1.getRuleList(rulesSetType1);
        Assertions.assertEquals(ruleListControl.get(0).getZone(), ruleList.get(0).getZone());
    }

    @Test
    void getResultCompleteRuleTest() {
        PresenceZone rule = new PresenceZone(1, "La zone 020 doit être présente", "020", true);
        List<Rule> ruleListControl = new ArrayList<>();
        ruleListControl.add(rule);

        //  Test du jeu de règles complet
        List<String> focusedRulesSet2 = new ArrayList<>();
        RulesSetType rulesSetType2 = new RulesSetType(false, true, focusedRulesSet2);
        RuleSet ruleSet2 = new RuleSet();
        List<Rule> ruleList = ruleSet2.getRuleList(rulesSetType2);
        Assertions.assertEquals(ruleListControl.get(0).getZone(), ruleList.get(1).getZone());
    }

    @Test
    void getResultFocusedRuleTest() {
        PresenceZone rule = new PresenceZone(1, "La zone 030 doit être présente", "030", true);
        List<Rule> ruleListControl = new ArrayList<>();
        ruleListControl.add(rule);

        //  Test du jeu de règles complet
        List<String> focusedRulesSet3 = new ArrayList<>();
        focusedRulesSet3.add("03");
        RulesSetType rulesSetType3 = new RulesSetType(false, false, focusedRulesSet3);
        RuleSet ruleSet3 = new RuleSet();
        List<Rule> ruleList = ruleSet3.getRuleList(rulesSetType3);
        Assertions.assertEquals(ruleListControl.get(0).getZone(), ruleList.get(0).getZone());
    }
}