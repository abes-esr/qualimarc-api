package fr.abes.qualimarc.core.model.entity.rules;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.rules.rulesSet.FocusedRulesSet;
import fr.abes.qualimarc.core.model.entity.rules.rulesSet.RulesSetType;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {RuleSet.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class RuleSetTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    @Test
    void getResultQuickRulesSet() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        //  Test du jeu de règles rapide
        FocusedRulesSet focusedRulesSet1 = new FocusedRulesSet(false, false, false);
        RulesSetType rulesSetType1 = new RulesSetType(true, false, focusedRulesSet1);
        RuleSet ruleSet1 = new RuleSet();
        ruleSet1.getRuleList(rulesSetType1);

        //  Test du jeu de règles complet
        FocusedRulesSet focusedRulesSet2 = new FocusedRulesSet(false, false, false);
        RulesSetType rulesSetType2 = new RulesSetType(false, true, focusedRulesSet2);
        RuleSet ruleSet2 = new RuleSet();
        ruleSet2.getRuleList(rulesSetType2);

        //  Test du jeu de règles ciblé
        FocusedRulesSet focusedRulesSet3 = new FocusedRulesSet(true, false, true);
        RulesSetType rulesSetType3 = new RulesSetType(false, false, focusedRulesSet3);
        RuleSet ruleSet3 = new RuleSet();
        ruleSet3.getRuleList(rulesSetType3);
    }
}