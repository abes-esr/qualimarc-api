package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.EnumChaineCaracteres;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(0, "999", "", EnumChaineCaracteres.STRICTEMENT, "");
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("Sous-zone absente")
    void isValid0() {
        PresenceChaineCaracteres presenceChaineCaracteres0 = new PresenceChaineCaracteres(0, "200", "g", EnumChaineCaracteres.STRICTEMENT, "");
        Assertions.assertFalse(presenceChaineCaracteres0.isValid(notice));
    }

    @Test
    @DisplayName("contient STRICTEMENT la chaine de caractères")
    void isValid1() {
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.STRICTEMENT, "Texte imprimé");
        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("ne contient pas STRICTEMENT la chaine de caractères")
    void isValid2() {
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.STRICTEMENT, "Texte imprime");
        Assertions.assertFalse(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("contient STRICTEMENT une des chaines de caractères")
    void isValid3() {
        ChaineCaracteres chaineCaracteres12 = new ChaineCaracteres(BooleanOperateur.OU, "Texte imprimé");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres12);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.STRICTEMENT, "Texte", listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne contient pas STRICTEMENT une des chaines de caractères")
    void isValid4() {
        ChaineCaracteres chaineCaracteres12 = new ChaineCaracteres(BooleanOperateur.OU, "Texte imprime");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres12);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.STRICTEMENT, "Texte", listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE par la chaine de caractères")
    void isValid5() {
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.COMMENCE, "Texte");
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne COMMENCE pas par la chaine de caractères")
    void isValid6() {
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.COMMENCE, "Convention");
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("COMMENCE par une des chaines de caractères")
    void isValid7() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.OU, "Texte");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.COMMENCE, "Convention", listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne COMMENCE pas par une des chaines de caractères")
    void isValid8() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.OU, "[brochure");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.COMMENCE, "Convention", listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE par la chaine de caractères")
    void isValid9() {
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.TERMINE, "imprimé");
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne TERMINE pas par la chaine de caractères")
    void isValid10() {
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.TERMINE, "imprime");
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("TERMINE par une des chaines de caractères")
    void isValid11() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.OU, "imprimé");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.TERMINE, "Convention", listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne TERMINE pas par une des chaines de caractères")
    void isValid12() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.OU, "[brochure");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.TERMINE, "Convention", listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT la chaine de caractères")
    void isValid13() {
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.CONTIENT, "xte imp");
        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne CONTIENT pas la chaine de caractères")
    void isValid14() {
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.CONTIENT, "xteimp");
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT une des chaines de caractères")
    void isValid15() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.OU, "xte imp");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.CONTIENT, "Convention", listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("CONTIENT toutes les chaines de caractères")
    void isValid16() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.ET, "xte imp");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.CONTIENT, "Texte", listChainesCaracteres);

        Assertions.assertTrue(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("ne CONTIENT pas les chaines de caractères")
    void isValid17() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.OU, "[brochure");
        Set<ChaineCaracteres> listChainesCaracteres = new HashSet<>();
        listChainesCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.CONTIENT, "Convention", listChainesCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }

    @Test
    @DisplayName("contient STRICTEMENT la chaine de caractères")
    void isValid18() {
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "300", "b", EnumChaineCaracteres.STRICTEMENT, "Test");
        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("test getZones")
    void getZones() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres();
        Set<ChaineCaracteres> list1ChaineCaracteres = new HashSet<>();
        list1ChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres rule = new PresenceChaineCaracteres(1, "020", "a", EnumChaineCaracteres.STRICTEMENT, "", list1ChaineCaracteres);

        Assertions.assertEquals("020$a", rule.getZones());
    }
}