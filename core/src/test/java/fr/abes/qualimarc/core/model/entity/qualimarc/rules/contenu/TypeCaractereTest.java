package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.TypeCaracteres;
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


@SpringBootTest(classes = {TypeCaractere.class})
class TypeCaractereTest {

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
    @DisplayName("on verifi qu'il renvoi bien 200$a dans le getZones")
    void getZones() {
        SimpleRule rule = new TypeCaractere(1,"200","a");

        Assertions.assertEquals("200$a",rule.getZones());
    }

    @Test
    @DisplayName("on verifi qu'il trouve bien des caracteres alphabetique dans la 300$b")
    void isValid() {
        TypeCaractere rule = new TypeCaractere(1,"300","b");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE);

        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il ne trouve pas de caracteres alphabetique dans la 033$d")
    void isValid2() {
        TypeCaractere rule = new TypeCaractere(1,"033","d");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE);

        Assertions.assertFalse(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il trouve de caracteres alphabetique avec accents dans la 200$x et $y")
    void isValid2etDemi() {
        TypeCaractere rule = new TypeCaractere(1,"200","x");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE);

        TypeCaractere rule1 = new TypeCaractere(1,"200","y");
        rule1.addTypeCaractere(TypeCaracteres.ALPHABETIQUE);

        Assertions.assertTrue(rule.isValid(notice));
        Assertions.assertTrue(rule1.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il trouve des caracteres alphabetique en Majuscule dans la 300$b")
    void isValid3() {
        TypeCaractere rule = new TypeCaractere(1,"300","b");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MAJ);

        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il ne trouve pas de caracteres alphabetique en Majuscule dans la 101$a")
    void isValid4() {
        TypeCaractere rule = new TypeCaractere(1,"101","a");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MAJ);

        Assertions.assertFalse(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il trouve de caracteres alphabetique maj avec accents dans la 200$y et pas dans la $x")
    void isValid4etDemi() {
        TypeCaractere rule = new TypeCaractere(1,"200","x");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MAJ);

        TypeCaractere rule1 = new TypeCaractere(1,"200","y");
        rule1.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MAJ);

        Assertions.assertFalse(rule.isValid(notice));
        Assertions.assertTrue(rule1.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il trouve des caracteres alphabetique en Minuscule dans la 300$b")
    void isValid5() {
        TypeCaractere rule = new TypeCaractere(1,"300","b");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MIN);

        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il ne trouve pas de caracteres alphabetique en Minuscule dans la 102$a")
    void isValid6() {
        TypeCaractere rule = new TypeCaractere(1,"102","a");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MIN);

        Assertions.assertFalse(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il trouve de caracteres alphabetique min avec accents dans la 200$x et pas dans la $y")
    void isValid6etDemi() {
        TypeCaractere rule = new TypeCaractere(1,"200","x");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MIN);

        TypeCaractere rule1 = new TypeCaractere(1,"200","y");
        rule1.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MIN);

        Assertions.assertTrue(rule.isValid(notice));
        Assertions.assertFalse(rule1.isValid(notice));
    }


    @Test
    @DisplayName("on verifi qu'il trouve des caracteres Numerique dans la 300$b")
    void isValid7() {
        TypeCaractere rule = new TypeCaractere(1,"035","a");
        rule.addTypeCaractere(TypeCaracteres.NUMERIQUE);

        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il ne trouve pas de caracteres Numerique dans la 102$a")
    void isValid8() {
        TypeCaractere rule = new TypeCaractere(1,"102","a");
        rule.addTypeCaractere(TypeCaracteres.NUMERIQUE);

        Assertions.assertFalse(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il trouve des caracteres Special dans la 033$a")
    void isValid9() {
        TypeCaractere rule = new TypeCaractere(1,"033","a");
        rule.addTypeCaractere(TypeCaracteres.SPECIAL);

        Assertions.assertTrue(rule.isValid(notice));
    }


    @Test
    @DisplayName("on verifi qu'il trouve des caracteres Special dans la 010$a & d et pas b")
    void isValid10() {
        TypeCaractere rule = new TypeCaractere(1,"010","a");
        rule.addTypeCaractere(TypeCaracteres.SPECIAL);

        Assertions.assertTrue(rule.isValid(notice));

        TypeCaractere rule1 = new TypeCaractere(1,"010","b");
        rule1.addTypeCaractere(TypeCaracteres.SPECIAL);

        Assertions.assertFalse(rule1.isValid(notice));

        TypeCaractere rule2 = new TypeCaractere(1,"010","d");
        rule2.addTypeCaractere(TypeCaracteres.SPECIAL);

        Assertions.assertTrue(rule2.isValid(notice));
    }


    @Test
    @DisplayName("on verifi qu'il trouve des caracteres Special dans la 181&a")
    void isValid11() {
        TypeCaractere rule = new TypeCaractere(1,"181","a");
        rule.addTypeCaractere(TypeCaracteres.SPECIAL);

        Assertions.assertTrue(rule.isValid(notice));
    }

    @Test
    @DisplayName("on verifi qu'il ne trouve pas de caracteres special dans la 200$x et $y")
    void isValid12() {
        TypeCaractere rule = new TypeCaractere(1,"200","x");
        rule.addTypeCaractere(TypeCaracteres.SPECIAL);

        TypeCaractere rule1 = new TypeCaractere(1,"200","y");
        rule1.addTypeCaractere(TypeCaracteres.SPECIAL);

        Assertions.assertFalse(rule.isValid(notice));
        Assertions.assertFalse(rule1.isValid(notice));
    }

    @Test
    @DisplayName("on verifi que Alphabetique et (Alphabetique min OU Maj) soit les memes")
    void isValid13() {
        TypeCaractere rule = new TypeCaractere(1,"214","c");
        rule.addTypeCaractere(TypeCaracteres.ALPHABETIQUE);

        TypeCaractere rule1 = new TypeCaractere(1,"214","c");
        rule1.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MIN);
        rule1.addTypeCaractere(TypeCaracteres.ALPHABETIQUE_MAJ);

        Assertions.assertEquals(rule.isValid(notice),rule1.isValid(notice));
    }
}