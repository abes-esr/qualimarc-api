package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.EnumTypeVerification;
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

@SpringBootTest(classes = {ComparaisonContenuSousZoneTest.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class ComparaisonContenuSousZoneTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    private NoticeXml notice;

    @BeforeEach
    void init() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        this.notice = mapper.readValue(xml, NoticeXml.class);
    }

    //  --------------------------
    //  test de l'enum STRICTEMENT
    //  --------------------------

    @Test
    @DisplayName("STRICTEMENT (zones différentes, sous-zone identiques) - True/False")
    void isValid01() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", "021", "a", EnumTypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "102", "a", "106", "a", EnumTypeVerification.STRICTEMENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT (zones différentes, sous-zone différentes) - True/False")
    void isValid02() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", "712", "4", EnumTypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "200", "e",  "210", "a", EnumTypeVerification.STRICTEMENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT (zones identiques, sous-zone identiques) - True/False")
    void isValid03() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", "182",  "6", EnumTypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", "300",  "a", EnumTypeVerification.STRICTEMENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT (zones identiques, sous-zone différentes) - True/False")
    void isValid04() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", "182", "a", EnumTypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", "182",  "a", EnumTypeVerification.STRICTEMENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    //  -----------------------
    //  test de l'enum CONTIENT
    //  -----------------------

    @Test
    @DisplayName("CONTIENT (zones différentes, sous-zone identiques) - True/False")
    void isValid05() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", "021", "a", EnumTypeVerification.CONTIENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", "021", "a", EnumTypeVerification.CONTIENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT (zones différentes, sous-zone différentes) - True/False")
    void isValid06() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", "712", "4", EnumTypeVerification.CONTIENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", "021", "a", EnumTypeVerification.CONTIENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT (zones identiques, sous-zone identiques) - True/False")
    void isValid07() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", "182", "6", EnumTypeVerification.CONTIENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", "300", "a", EnumTypeVerification.CONTIENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT (zones identiques, sous-zone différentes) - True/False")
    void isValid08() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", "182", "a", EnumTypeVerification.CONTIENT);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", "182",  "a", EnumTypeVerification.CONTIENT);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    //  ----------------------------
    //  test de l'enum NECONTIENTPAS
    //  ----------------------------

    @Test
    @DisplayName("NECONTIENTPAS (zones différentes, sous-zone identiques) - True/False")
    void isValid09() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", "021", "a", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", "021", "a", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS (zones différentes, sous-zone différentes) - True/False")
    void isValid10() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", "712", "4", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", "021", "a", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS (zones identiques, sous-zone identiques) - True/False")
    void isValid11() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", "182", "6", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", "300", "a", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS (zones identiques, sous-zone différentes) - True/False")
    void isValid12() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", "182", "a", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", "182",  "a", EnumTypeVerification.NECONTIENTPAS);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    //  -----------------------
    //  test de l'enum COMMENCE
    //  -----------------------

    @Test
    @DisplayName("COMMENCE (zones différentes, sous-zone identiques) - True/False")
    void isValid13() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", "021", "a", EnumTypeVerification.COMMENCE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", "021", "a", EnumTypeVerification.COMMENCE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE (zones différentes, sous-zone différentes) - True/False")
    void isValid14() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", "712", "4", EnumTypeVerification.COMMENCE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", "021", "a", EnumTypeVerification.COMMENCE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE (zones identiques, sous-zone identiques) - True/False")
    void isValid15() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", "182", "6", EnumTypeVerification.COMMENCE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", "300", "a", EnumTypeVerification.COMMENCE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE (zones identiques, sous-zone différentes) - True/False")
    void isValid16() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", "182", "a", EnumTypeVerification.COMMENCE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", "182",  "a", EnumTypeVerification.COMMENCE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    //  -----------------------
    //  test de l'enum TERMINE
    //  -----------------------

    @Test
    @DisplayName("TERMINE (zones différentes, sous-zone identiques) - True/False")
    void isValid17() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", "021", "a", EnumTypeVerification.TERMINE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", "021", "a", EnumTypeVerification.TERMINE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE (zones différentes, sous-zone différentes) - True/False")
    void isValid18() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", "712", "4", EnumTypeVerification.TERMINE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", "021", "a", EnumTypeVerification.TERMINE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE (zones identiques, sous-zone identiques) - True/False")
    void isValid19() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", "182", "6", EnumTypeVerification.TERMINE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", "300", "a", EnumTypeVerification.TERMINE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE (zones identiques, sous-zone différentes) - True/False")
    void isValid20() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", "182", "a", EnumTypeVerification.TERMINE);
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", "182",  "a", EnumTypeVerification.TERMINE);
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("Test de la récupération des zones")
    void getZones() {
        ComparaisonContenuSousZone rule2 =  new ComparaisonContenuSousZone(1, "300", "a", "400", "b", EnumTypeVerification.STRICTEMENT);
        Assertions.assertEquals("300$a - 400$b", rule2.getZones());
    }
}