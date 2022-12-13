package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {NombreSousZone.class})
public class NombreSousZoneTest {
    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice1;

    NoticeXml notice;

    @BeforeEach
    void init() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        this.notice = mapper.readValue(xml, NoticeXml.class);
    }

    @Test
    void isValidMemeNombre() {
        SimpleRule rule1 = new NombreSousZone(1, "606", "3", "712", "3");
        Assertions.assertTrue(rule1.isValid(notice));
        SimpleRule rule2 = new NombreSousZone(1, "606", "a", "676", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    void testIsValidSousZoneIntrouvable() {
        //si la zone sous zone n'existe pas on renvoie 0 occurrences
        SimpleRule rule1 = new NombreSousZone(1, "606", "b", "676", "b");
        Assertions.assertFalse(rule1.isValid(notice));
    }

    @Test
    void testIsValidZoneSourceRepetee() {
        SimpleRule rule1 = new NombreSousZone(1, "300", "a", "801", "b");
        Assertions.assertFalse(rule1.isValid(notice));
    }

    @Test
    void testIsValidZoneCibleRepetee() {
        SimpleRule rule1 = new NombreSousZone(1, "801", "b", "300", "b");
        Assertions.assertTrue(rule1.isValid(notice));
    }

    @Test
    void testIsValidZonesAbsents() {
        SimpleRule rule1 = new NombreSousZone(1, "675", "a", "674", "a");
        Assertions.assertFalse(rule1.isValid(notice));
    }

    @Test
    void getZones() {
        NombreSousZone rule = new NombreSousZone(1, "600", "a", "702", "b");
        Assertions.assertEquals(2, rule.getZones().size());
        Assertions.assertEquals("600$a", rule.getZones().get(0));
        Assertions.assertEquals("702$b", rule.getZones().get(1));
    }
}