package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.core.utils.Priority;
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
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
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
        NombreZone rule1 = new NombreZone("La notice doit contenir 3 zones 181 mais n'en contient que 2", "181", Priority.P1, Operateur.EGAL, 3);
        Assertions.assertFalse(rule1.isValid(notice));

        NombreZone rule2 = new NombreZone("La notice doit contenir 2 zones 181 et en contient 2", "181", Priority.P1, Operateur.EGAL, 2);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("Test nombre de zones opérateur SUPERIEUR")
    void testIsValidSuperieur() {
        NombreZone rule1 = new NombreZone("La notice doit contenir plus de 2 zones 181 et en contient 2", "181", Priority.P1, Operateur.SUPERIEUR, 2);
        Assertions.assertFalse(rule1.isValid(notice));

        NombreZone rule2 = new NombreZone("La notice doit contenir plus de 1 zones 181 et en contient 2", "181", Priority.P1, Operateur.SUPERIEUR, 1);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("Test nombre de zones opérateur INFERIEUR")
    void testIsValidInferieur() {
        NombreZone rule1 = new NombreZone("La notice doit contenir moins de 2 zones 181 et en contient 2", "181", Priority.P1, Operateur.INFERIEUR, 2);
        Assertions.assertFalse(rule1.isValid(notice));

        NombreZone rule2 = new NombreZone("La notice doit contenir moins de 3 zones 181 et en contient 2", "181", Priority.P1, Operateur.INFERIEUR, 3);
        Assertions.assertTrue(rule2.isValid(notice));
    }
}
