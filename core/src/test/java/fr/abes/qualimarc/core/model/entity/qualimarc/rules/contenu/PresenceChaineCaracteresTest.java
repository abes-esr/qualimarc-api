package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.TypeVerification;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;

@SpringBootTest(classes = {PresenceChaineCaracteres.class})
public class PresenceChaineCaracteresTest {

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
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(0, "999", "", TypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("Sous-zone absente")
    void isValid0() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte imprimé", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres0 = new PresenceChaineCaracteres(0, "200", "g", TypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres0.isValid(notice));
    }

    @Test
    @DisplayName("contient STRICTEMENT la chaine de caractères")
    void isValid1() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte imprimé", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("ne contient pas STRICTEMENT la chaine de caractères")
    void isValid2() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte imprime", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("contient STRICTEMENT une des chaines de caractères")
    void isValid3() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(2, BooleanOperateur.OU, "Texte imprimé", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.STRICTEMENT, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne contient pas STRICTEMENT une des chaines de caractères")
    void isValid4() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(2, BooleanOperateur.OU, "Texte imprime", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.STRICTEMENT, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE par la chaine de caractères")
    void isValid5() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.COMMENCE, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne COMMENCE pas par la chaine de caractères")
    void isValid6() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Convention", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.COMMENCE, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE par une des chaines de caractères")
    void isValid7() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(2, BooleanOperateur.OU, "Texte", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.COMMENCE, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne COMMENCE pas par une des chaines de caractères")
    void isValid8() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(2, BooleanOperateur.OU, "[brochure", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.COMMENCE, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE par la chaine de caractères")
    void isValid9() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "imprimé", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.TERMINE, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne TERMINE pas par la chaine de caractères")
    void isValid10() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "imprime", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.TERMINE, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE par une des chaines de caractères")
    void isValid11() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(2, BooleanOperateur.OU, "imprimé", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.TERMINE, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne TERMINE pas par une des chaines de caractères")
    void isValid12() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(2, BooleanOperateur.OU, "[brochure", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.TERMINE, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT la chaine de caractères")
    void isValid13() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "xte imp", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.CONTIENT, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne CONTIENT pas la chaine de caractères")
    void isValid14() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "xteimp", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.CONTIENT, listChaineCaracteres);
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT une des chaines de caractères")
    void isValid15() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Convention", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(2, BooleanOperateur.OU, "xte imp", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.CONTIENT, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT toutes les chaines de caractères")
    void isValid16() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(1, BooleanOperateur.ET, "xte imp", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.CONTIENT, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne CONTIENT pas les chaines de caractères")
    void isValid17() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, BooleanOperateur.OU, "Convention", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(1, BooleanOperateur.OU, "[brochure", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.CONTIENT, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("La deuxième occurence de la zone contient STRICTEMENT la chaine de caractères")
    void isValid18() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Test des TU", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(1, BooleanOperateur.OU, "Test", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        listChaineCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "990", "b", TypeVerification.STRICTEMENT, listChaineCaracteres);
        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS la chaine de caractères")
    void isValid19() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Convention", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.NECONTIENTPAS, listChaineCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne NECONTIENTPAS pas la chaine de caractères")
    void isValid20() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(1, "Texte", null);
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.NECONTIENTPAS, listChaineCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS une des chaines de caractères")
    void isValid21() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(1, BooleanOperateur.OU, "Convention", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "990", "b", TypeVerification.NECONTIENTPAS, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne NECONTIENTPAS pas une des chaines de caractères")
    void isValid22() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "Texte", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(1, BooleanOperateur.OU, "imprimé", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.NECONTIENTPAS, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("NECONTIENTPAS toutes les chaines de caractères")
    void isValid23() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1, "collective", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(1, BooleanOperateur.ET, "Convention", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.NECONTIENTPAS, listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne NECONTIENTPAS pas toutes les chaines de caractères")
    void isValid24() {
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(1,"imprimé", null);
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(1, BooleanOperateur.ET, "Texte", null);
        TreeSet<ChaineCaracteres> listChainesCaracteres = new TreeSet<>();
        listChainesCaracteres.add(chaineCaracteres1);
        listChainesCaracteres.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", TypeVerification.NECONTIENTPAS, listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("test getZones")
    void getZones() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres();
        TreeSet<ChaineCaracteres> list1ChaineCaracteres = new TreeSet<>();
        list1ChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres rule = new PresenceChaineCaracteres(1, "020", "a", TypeVerification.STRICTEMENT, list1ChaineCaracteres);

        Assertions.assertEquals(1, rule.getZones().size());
        Assertions.assertEquals("020$a", rule.getZones().get(0));
    }
}
