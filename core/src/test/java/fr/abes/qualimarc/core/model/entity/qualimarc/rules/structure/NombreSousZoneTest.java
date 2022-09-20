package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.utils.Priority;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {NombreSousZone.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class NombreSousZoneTest {
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
    void testIsValidMemeNombre() {
        Rule rule1 = new NombreSousZone(1, "La notice doit contenir autant de 606 $3 que de 712 $3", "606", Priority.P1, "3", "712", "3");
        Assertions.assertTrue(rule1.isValid(notice));
        Rule rule2 = new NombreSousZone(1, "La notice doit contenir autant 606 $a de que de 676 $a", "606", Priority.P1, "a", "676", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    void testIsValidSousZoneIntrouvable() {
        //si la zone sous zone n'existe pas on renvoie 0 occurrences
        Rule rule1 = new NombreSousZone(1, "La notice doit contenir autant de 606 $b que de 676 $b", "606", Priority.P1, "b", "676", "b");
        Assertions.assertFalse(rule1.isValid(notice));
    }

    @Test
    void testIsValidZoneSourceRepetee() {
        Rule rule1 = new NombreSousZone(1, "La notice doit contenir autant de 300 $a que de 801 $b", "300", Priority.P1, "a", "801", "b");
        Assertions.assertFalse(rule1.isValid(notice));
    }

    @Test
    void testIsValidZoneCibleRepetee() {
        Rule rule1 = new NombreSousZone(1, "La notice doit contenir autant de 801 $b que de 300 $b", "801", Priority.P1, "b", "300", "b");
        Assertions.assertTrue(rule1.isValid(notice));
    }

    @Test
    void testIsValidZonesAbsents() {
        Rule rule1 = new NombreSousZone(1, "aucune des zones n'est présente dans la notice", "675", Priority.P1, "a", "674", "a");
        Assertions.assertFalse(rule1.isValid(notice));
    }
}