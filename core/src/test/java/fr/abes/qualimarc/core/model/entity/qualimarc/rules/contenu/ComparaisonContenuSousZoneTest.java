package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.TypeVerification;
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


    //  -------
    //  test PO
    //  -------
    @Test
    @DisplayName("Test PO")
    void isValid00() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "338", "c", TypeVerification.STRICTEMENT, "338", "b");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "600", "a", TypeVerification.STRICTEMENTDIFFERENT, "338", "b");
        Assertions.assertTrue(rule2.isValid(notice));

        ComparaisonContenuSousZone rule3 = new ComparaisonContenuSousZone(1, "200", "y", TypeVerification.COMMENCE, 1, "205", "a");
        Assertions.assertTrue(rule3.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT (zones différentes, sous-zone identiques) - True/False")
    void isValid01() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", TypeVerification.STRICTEMENT, "021", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "102", "a", TypeVerification.STRICTEMENT, "106", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT (zones différentes, sous-zone différentes) - True/False")
    void isValid02() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", TypeVerification.STRICTEMENT, "712", "4");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "200", "e", TypeVerification.STRICTEMENT,  "210", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT (zones identiques, sous-zone identiques) - True/False")
    void isValid03() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.STRICTEMENT, "182",  "6");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", TypeVerification.STRICTEMENT, "300",  "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT (zones identiques, sous-zone différentes) - True/False")
    void isValid04() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", TypeVerification.STRICTEMENT, "182", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.STRICTEMENT, "182",  "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    //  -----------------------
    //  test de l'enum CONTIENT
    //  -----------------------

    @Test
    @DisplayName("CONTIENT (zones différentes, sous-zone identiques) - True/False")
    void isValid05() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", TypeVerification.CONTIENT, "021", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", TypeVerification.CONTIENT, "021", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT (zones différentes, sous-zone différentes) - True/False")
    void isValid06() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", TypeVerification.CONTIENT, "712", "4");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", TypeVerification.CONTIENT, "021", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT (zones identiques, sous-zone identiques) - True/False")
    void isValid07() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.CONTIENT, "182", "6");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", TypeVerification.CONTIENT, "300", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT (zones identiques, sous-zone différentes) - True/False")
    void isValid08() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", TypeVerification.CONTIENT, "182", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.CONTIENT, "182",  "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    //  ----------------------------
    //  test de l'enum NECONTIENTPAS
    //  ----------------------------

    @Test
    @DisplayName("NECONTIENTPAS (zones différentes, sous-zone identiques) - True/False")
    void isValid09() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", TypeVerification.NECONTIENTPAS, "021", "a");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", TypeVerification.NECONTIENTPAS, "021", "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS (zones différentes, sous-zone différentes) - True/False")
    void isValid10() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", TypeVerification.NECONTIENTPAS, "712", "4");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", TypeVerification.NECONTIENTPAS, "021", "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS (zones identiques, sous-zone identiques) - True/False")
    void isValid11() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.NECONTIENTPAS, "182", "6");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", TypeVerification.NECONTIENTPAS, "300", "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS (zones identiques, sous-zone différentes) - True/False")
    void isValid12() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", TypeVerification.NECONTIENTPAS, "182", "a");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.NECONTIENTPAS, "182",  "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    //  -----------------------
    //  test de l'enum COMMENCE
    //  -----------------------

    @Test
    @DisplayName("COMMENCE (zones différentes, sous-zone identiques) - True/False")
    void isValid13() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", TypeVerification.COMMENCE, "021", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", TypeVerification.COMMENCE, "021", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE (zones différentes, sous-zone différentes) - True/False")
    void isValid14() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", TypeVerification.COMMENCE, "712", "4");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", TypeVerification.COMMENCE, "021", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE (zones identiques, sous-zone identiques) - True/False")
    void isValid15() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.COMMENCE, "182", "6");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", TypeVerification.COMMENCE, "300", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE (zones identiques, sous-zone différentes) - True/False")
    void isValid16() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", TypeVerification.COMMENCE, "182", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.COMMENCE, "182",  "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    //  -----------------------
    //  test de l'enum TERMINE
    //  -----------------------

    @Test
    @DisplayName("TERMINE (zones différentes, sous-zone identiques) - True/False")
    void isValid17() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", TypeVerification.TERMINE, "021", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "010", "a", TypeVerification.TERMINE, "021", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE (zones différentes, sous-zone différentes) - True/False")
    void isValid18() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", TypeVerification.TERMINE, "712", "4");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "020", "b", TypeVerification.TERMINE, "021", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE (zones identiques, sous-zone identiques) - True/False")
    void isValid19() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.TERMINE, "182", "6");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", TypeVerification.TERMINE, "300", "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE (zones identiques, sous-zone différentes) - True/False")
    void isValid20() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", TypeVerification.TERMINE, "182", "a");
        Assertions.assertTrue(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.TERMINE, "182",  "a");
        Assertions.assertFalse(rule2.isValid(notice));
    }

    //  -----------------------------------
    //  test de l'enum STRICTEMENTDIFFERENT
    //  -----------------------------------

    @Test
    @DisplayName("STRICTEMENTDIFFERENT (zones différentes, sous-zone identiques) - True/False")
    void isValid21() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "020", "a", TypeVerification.STRICTEMENTDIFFERENT, "021", "a");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "102", "a", TypeVerification.STRICTEMENTDIFFERENT, "106", "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENTDIFFERENT (zones différentes, sous-zone différentes) - True/False")
    void isValid22() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "686", "a", TypeVerification.STRICTEMENTDIFFERENT, "712", "4");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "200", "e", TypeVerification.STRICTEMENTDIFFERENT,  "210", "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENTDIFFERENT (zones identiques, sous-zone identiques) - True/False")
    void isValid23() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.STRICTEMENTDIFFERENT, "182",  "6");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "300", "a", TypeVerification.STRICTEMENTDIFFERENT, "300",  "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENTDIFFERENT (zones identiques, sous-zone différentes) - True/False")
    void isValid24() {
        ComparaisonContenuSousZone rule1 = new ComparaisonContenuSousZone(1, "182", "c", TypeVerification.STRICTEMENTDIFFERENT, "182", "a");
        Assertions.assertFalse(rule1.isValid(notice));

        ComparaisonContenuSousZone rule2 = new ComparaisonContenuSousZone(1, "182", "6", TypeVerification.STRICTEMENTDIFFERENT, "182",  "a");
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("Test de la récupération des zones")
    void getZones() {
        ComparaisonContenuSousZone rule2 =  new ComparaisonContenuSousZone(1, "300", "a", TypeVerification.STRICTEMENT, "400", "b");
        Assertions.assertEquals("300$a - 400$b", rule2.getZones());
    }
}