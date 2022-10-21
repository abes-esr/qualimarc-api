package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.Operateur;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {NombreCaracteres.class})
class NombreCaracteresTest {
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
    @DisplayName("Test isValid cas zone absente, sous zone absente")
    void isValid1() {
        NombreCaracteres rule = new NombreCaracteres(1, "011", "c", Operateur.SUPERIEUR, 1);
        Assertions.assertFalse(rule.isValid(notice));
    }

    @Test
    @DisplayName("Test isValid cas zone présente, sous zone absente")
    void isValid2() {
        NombreCaracteres rule = new NombreCaracteres(1, "010", "c", Operateur.SUPERIEUR, 1);
        Assertions.assertFalse(rule.isValid(notice));
    }

    @Test
    @DisplayName("test isValid ok Opérateur EGAL")
    void isValid3() {
        NombreCaracteres rule = new NombreCaracteres(1, "200", "a", Operateur.EGAL, 47);
        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("test isValid ok Opérateur SUPERIEUR")
    void isValid4() {
        NombreCaracteres rule = new NombreCaracteres(1, "200", "a", Operateur.SUPERIEUR, 1);
        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("test isValid ok Opérateur INFERIEUR")
    void isValid5() {
        NombreCaracteres rule = new NombreCaracteres(1, "200", "a", Operateur.INFERIEUR, 100);
        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("test isValid ok sur plusieurs sous zones dans une même zone")
    void isValid6() {
        NombreCaracteres rule = new NombreCaracteres(1, "606", "x", Operateur.EGAL, 9);
        Assertions.assertTrue(rule.isValid(notice));
        NombreCaracteres rule2 = new NombreCaracteres(1, "606", "x", Operateur.EGAL, 23);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    @DisplayName("test isValid ok sur zones répétées")
    void isValid7() {
        NombreCaracteres rule = new NombreCaracteres(1, "300", "a", Operateur.EGAL, 28);
        Assertions.assertTrue(rule.isValid(notice));
        NombreCaracteres rule2 = new NombreCaracteres(1, "300", "a", Operateur.EGAL, 32);
        Assertions.assertTrue(rule2.isValid(notice));
    }

    @Test
    void getZones() {
        NombreCaracteres rule = new NombreCaracteres(1, "200", "a", Operateur.SUPERIEUR, 1);
        Assertions.assertEquals("200$a", rule.getZones());
    }
}