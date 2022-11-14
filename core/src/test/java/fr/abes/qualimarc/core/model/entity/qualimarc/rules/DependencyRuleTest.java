package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringJUnitConfig(classes = {DependencyRule.class})
public class DependencyRuleTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    @Test
    @DisplayName("Récupération du ppn")
    void getPpnNoticeLiee() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        DependencyRule rule1 = new DependencyRule(1, "606", "3", 1, new ComplexRule());
        Assertions.assertEquals("02787088X", rule1.getPpnsNoticeLiee(notice).get(0));
        Assertions.assertEquals("02731667X", rule1.getPpnsNoticeLiee(notice).get(1));
        Assertions.assertEquals("027226794", rule1.getPpnsNoticeLiee(notice).get(2));

        DependencyRule rule2 = new DependencyRule(1, "606", "8", 1, new ComplexRule());
        Assertions.assertEquals(0, rule2.getPpnsNoticeLiee(notice).size());

        DependencyRule rule3 = new DependencyRule(1, "607", "3", 1, new ComplexRule());
        Assertions.assertEquals(0, rule3.getPpnsNoticeLiee(notice).size());
    }

    @Test
    @DisplayName("Récupération de la zone")
    void getZone() {
        DependencyRule rule1 = new DependencyRule(1, "607", "3", 1, new ComplexRule());
        Assertions.assertEquals("606$3", rule1.getZones());
    }
}