package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
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


@SpringBootTest(classes = {ComparaisonDate.class})
class ComparaisonDateTest {

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
    @DisplayName("Test sur la meme zone")
    void isValidOnSameZone() {
        ComparaisonDate comparaisonDate= new ComparaisonDate(1, "100", "a", 0,3,"100", "a",0,3, ComparaisonOperateur.EGAL);
        Assertions.assertTrue(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR_EGAL);
        Assertions.assertTrue(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR_EGAL);
        Assertions.assertTrue(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.DIFFERENT);
        Assertions.assertFalse(comparaisonDate.isValid(notice));
    }

    @Test
    @DisplayName("Test sur des zones differentes 100$a est INFERIEUR a 035$d")
    void isValidOnZone1InferieurZone2() {
        ComparaisonDate comparaisonDate= new ComparaisonDate(1, "100", "a", 0,3,"035", "d",0,3, ComparaisonOperateur.EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR);
        Assertions.assertTrue(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR_EGAL);
        Assertions.assertTrue(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.DIFFERENT);
        Assertions.assertTrue(comparaisonDate.isValid(notice));
    }

    @Test
    @DisplayName("Test sur des zones differentes 100$a est SUPERIEUR a 035$d")
    void isValidOnZone1SuperieurZone2() {
        ComparaisonDate comparaisonDate= new ComparaisonDate(1, "035", "d", 0,3,"100", "a",0,3, ComparaisonOperateur.EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR);
        Assertions.assertTrue(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR_EGAL);
        Assertions.assertTrue(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.DIFFERENT);
        Assertions.assertTrue(comparaisonDate.isValid(notice));
    }

    @Test
    @DisplayName("Test sur des zones differentes 210$d est INFERIEUR à 214$d sans les positiosn (214&d ne contient pas de date)")
    void isValidOnZone1InferieurZone2SansPosition() {
        ComparaisonDate comparaisonDate= new ComparaisonDate(1, "210", "d", "214", "d", ComparaisonOperateur.EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.DIFFERENT);
        Assertions.assertFalse(comparaisonDate.isValid(notice));
    }

    @Test
    @DisplayName("Test sur des zones differentes mais 801$c est avec un X")
    void isValidOnZoneAvecX() {
        ComparaisonDate comparaisonDate= new ComparaisonDate(1, "801", "c", 0, 3, "210", "d", ComparaisonOperateur.EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.DIFFERENT);
        Assertions.assertFalse(comparaisonDate.isValid(notice));
    }

    @Test
    @DisplayName("Test sur une zone qui n'existe pas")
    void isValidOnZoneInexistante() {
        ComparaisonDate comparaisonDate= new ComparaisonDate(1, "211", "a", "210", "d", ComparaisonOperateur.EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.SUPERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.INFERIEUR_EGAL);
        Assertions.assertFalse(comparaisonDate.isValid(notice));

        comparaisonDate.setComparateur(ComparaisonOperateur.DIFFERENT);
        Assertions.assertFalse(comparaisonDate.isValid(notice));
    }

    @Test
    void getZones() {
        ComparaisonDate comparaisonDate= new ComparaisonDate(1, "100", "a", "210", "d", ComparaisonOperateur.EGAL);
        Assertions.assertEquals(2, comparaisonDate.getZones().size());
        Assertions.assertEquals("100$a", comparaisonDate.getZones().get(0));
        Assertions.assertEquals("210$d", comparaisonDate.getZones().get(1));
    }
}