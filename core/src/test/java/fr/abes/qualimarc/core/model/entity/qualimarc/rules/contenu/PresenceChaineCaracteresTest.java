package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
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
import java.util.*;

@SpringBootTest(classes = {PresenceChaineCaracteres.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class PresenceChaineCaracteresTest {

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

    @Test
    @DisplayName("Zone absente")
    void isValid() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte imprimé", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(0, "999", "", EnumTypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("Sous-zone absente")
    void isValid0() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte imprimé", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres0 = new PresenceChaineCaracteres(0, "200", "g", EnumTypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres0.isValid(notice));
    }

    @Test
    @DisplayName("contient STRICTEMENT la chaine de caractères")
    void isValid1() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte imprimé", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("ne contient pas STRICTEMENT la chaine de caractères")
    void isValid2() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte imprime", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("contient STRICTEMENT une des chaines de caractères")
    void isValid3() {
        ChaineCaracteres chaineCaracteres3a = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres3b = new ChaineCaracteres(2, BooleanOperateur.OU, "Texte imprimé", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres3a);
        listChainesCaracteres.add(chaineCaracteres3b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.STRICTEMENT, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne contient pas STRICTEMENT une des chaines de caractères")
    void isValid4() {
        ChaineCaracteres chaineCaracteres4a = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres4b = new ChaineCaracteres(2, BooleanOperateur.OU, "Texte imprime", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres4a);
        listChainesCaracteres.add(chaineCaracteres4b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.STRICTEMENT, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE par la chaine de caractères")
    void isValid5() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.COMMENCE, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne COMMENCE pas par la chaine de caractères")
    void isValid6() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Convention", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.COMMENCE, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE par une des chaines de caractères")
    void isValid7() {
        ChaineCaracteres chaineCaracteres7a = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres7b = new ChaineCaracteres(2, BooleanOperateur.OU, "Texte", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres7a);
        listChainesCaracteres.add(chaineCaracteres7b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.COMMENCE, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne COMMENCE pas par une des chaines de caractères")
    void isValid8() {
        ChaineCaracteres chaineCaracteres8a = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres8b = new ChaineCaracteres(2, BooleanOperateur.OU, "[brochure", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres8a);
        listChainesCaracteres.add(chaineCaracteres8b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.COMMENCE, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE par la chaine de caractères")
    void isValid9() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "imprimé", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.TERMINE, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne TERMINE pas par la chaine de caractères")
    void isValid10() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "imprime", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.TERMINE, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE par une des chaines de caractères")
    void isValid11() {
        ChaineCaracteres chaineCaracteres11a = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres11b = new ChaineCaracteres(2, BooleanOperateur.OU, "imprimé", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres11a);
        listChainesCaracteres.add(chaineCaracteres11b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.TERMINE, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne TERMINE pas par une des chaines de caractères")
    void isValid12() {
        ChaineCaracteres chaineCaracteres12a = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres12b = new ChaineCaracteres(2, BooleanOperateur.OU, "[brochure", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres12a);
        listChainesCaracteres.add(chaineCaracteres12b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.TERMINE, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT la chaine de caractères")
    void isValid13() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "xte imp", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.CONTIENT, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne CONTIENT pas la chaine de caractères")
    void isValid14() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "xteimp", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.CONTIENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT une des chaines de caractères")
    void isValid15() {
        ChaineCaracteres chaineCaracteres15a = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres15b = new ChaineCaracteres(2, BooleanOperateur.OU, "xte imp", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres15a);
        listChainesCaracteres.add(chaineCaracteres15b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.CONTIENT, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT toutes les chaines de caractères")
    void isValid16() {
        ChaineCaracteres chaineCaracteres16a = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres16b = new ChaineCaracteres(1, BooleanOperateur.ET, "xte imp", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres16a);
        listChainesCaracteres.add(chaineCaracteres16b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.CONTIENT, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne CONTIENT pas les chaines de caractères")
    void isValid17() {
        ChaineCaracteres chaineCaracteres17a = new ChaineCaracteres(1, BooleanOperateur.OU, "Convention", null);
        ChaineCaracteres chaineCaracteres17b = new ChaineCaracteres(1, BooleanOperateur.OU, "[brochure", null);
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres17a);
        listChainesCaracteres.add(chaineCaracteres17b);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.CONTIENT, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("La deuxième occurence de la zone contient STRICTEMENT la chaine de caractères")
    void isValid18() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Test", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "300", "b", EnumTypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));
    }







    @Test
    @DisplayName("NECONTIENTPAS la chaine de caractères")
    void isValid19() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Convention", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.NECONTIENTPAS, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne NECONTIENTPAS pas la chaine de caractères")
    void isValid20() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte", null);
        Set<ChaineCaracteres> listChaineCaracteres = new HashSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.NECONTIENTPAS, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    // TODO continuer à adapter les TU
//    @Test
//    @DisplayName("NECONTIENTPAS une des chaines de caractères")
//    void isValid21() {
//        ChaineCaracteres chaineCaracteres15a = new ChaineCaracteres(1, "Convention", null);
//        ChaineCaracteres chaineCaracteres15b = new ChaineCaracteres(2, BooleanOperateur.OU, "collective", null);
//        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
//        listChainesCaracteres.add(chaineCaracteres15a);
//        listChainesCaracteres.add(chaineCaracteres15b);
//        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.NECONTIENTPAS, listChainesCaracteres);
//
//        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
//    }
//
//    @Test
//    @DisplayName("NECONTIENTPAS toutes les chaines de caractères")
//    void isValid22() {
//        ChaineCaracteres chaineCaracteres16a = new ChaineCaracteres(1, "collective", null);
//        ChaineCaracteres chaineCaracteres16b = new ChaineCaracteres(1, BooleanOperateur.ET, "Convention", null);
//        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
//        listChainesCaracteres.add(chaineCaracteres16a);
//        listChainesCaracteres.add(chaineCaracteres16b);
//        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.NECONTIENTPAS, listChainesCaracteres);
//
//        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
//    }
//
//    @Test
//    @DisplayName("ne CONTIENT pas les chaines de caractères")
//    void isValid23() {
//        ChaineCaracteres chaineCaracteres17a = new ChaineCaracteres(1,"Convention", null);
//        ChaineCaracteres chaineCaracteres17b = new ChaineCaracteres(1, BooleanOperateur.OU, "Texte", null);
//        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
//        listChainesCaracteres.add(chaineCaracteres17a);
//        listChainesCaracteres.add(chaineCaracteres17b);
//        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumTypeVerification.NECONTIENTPAS, listChainesCaracteres);
//
//        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
//    }







    @Test
    @DisplayName("test getZones")
    void getZones() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres();
        Set<ChaineCaracteres> list1ChaineCaracteres = new HashSet<>();
        list1ChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres rule = new PresenceChaineCaracteres(1, "020", "a", EnumTypeVerification.STRICTEMENT, list1ChaineCaracteres);

        Assertions.assertEquals("020$a", rule.getZones());
    }
}