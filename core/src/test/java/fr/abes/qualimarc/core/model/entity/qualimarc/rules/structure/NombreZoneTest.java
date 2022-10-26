package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.Operateur;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {NombreZone.class})
public class NombreZoneTest {
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
    @DisplayName("Test nombre de zones opérateur EGAL")
    void testIsValidEgal() {
        SimpleRule rule1 = new NombreZone(1, "181", Operateur.EGAL, 3);
        Assertions.assertFalse(rule1.isValid(notice));

        SimpleRule rule2 = new NombreZone(1, "181", Operateur.EGAL, 2);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("Test nombre de zones opérateur SUPERIEUR")
    void testIsValidSuperieur() {
        SimpleRule rule1 = new NombreZone(1,  "181", Operateur.SUPERIEUR, 2);
        Assertions.assertFalse(rule1.isValid(notice));

        SimpleRule rule2 = new NombreZone(1, "181", Operateur.SUPERIEUR, 1);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("Test nombre de zones opérateur INFERIEUR")
    void testIsValidInferieur() {
        SimpleRule rule1 = new NombreZone(1, "181", Operateur.INFERIEUR, 2);
        Assertions.assertFalse(rule1.isValid(notice));

        SimpleRule rule2 = new NombreZone(1, "181", Operateur.INFERIEUR, 3);
        Assertions.assertTrue(rule2.isValid(notice));
    }
}
