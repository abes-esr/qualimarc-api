package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.TypeNoticeLiee;
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
import java.util.List;
import java.util.stream.Collectors;

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

        DependencyRule rule1 = new DependencyRule(1, "606", "3", TypeNoticeLiee.AUTORITE, 1, new ComplexRule());
        Assertions.assertTrue( rule1.getPpnsNoticeLiee(notice).stream().findFirst().isPresent());
        Assertions.assertEquals("02787088X", rule1.getPpnsNoticeLiee(notice).stream().findFirst().get());

        DependencyRule rule2 = new DependencyRule(1, "606", "8", TypeNoticeLiee.AUTORITE, 1, new ComplexRule());
        Assertions.assertEquals(0, rule2.getPpnsNoticeLiee(notice).size());

        DependencyRule rule3 = new DependencyRule(1, "608", "3", TypeNoticeLiee.AUTORITE, 1, new ComplexRule());
        Assertions.assertEquals(0, rule3.getPpnsNoticeLiee(notice).size());

        DependencyRule rule4 = new DependencyRule(1, "607", "3", TypeNoticeLiee.AUTORITE, 1, new ComplexRule());
        List<String> listePpn = rule4.getPpnsNoticeLiee(notice).stream().sorted().collect(Collectors.toList());
        Assertions.assertEquals(3, listePpn.size());
        Assertions.assertEquals("02731667X", listePpn.get(0));
        Assertions.assertEquals("02787088X", listePpn.get(1));
        Assertions.assertEquals("987654321", listePpn.get(2));
    }

    @Test
    @DisplayName("Récupération de la zone")
    void getZone() {
        DependencyRule rule1 = new DependencyRule(1, "607", "3", TypeNoticeLiee.AUTORITE, 1, new ComplexRule());
        Assertions.assertEquals(1, rule1.getZones().size());
        Assertions.assertEquals("607$3", rule1.getZones().get(0));
    }
}