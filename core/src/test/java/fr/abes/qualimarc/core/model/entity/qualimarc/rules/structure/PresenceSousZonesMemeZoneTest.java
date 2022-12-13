package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.souszoneoperator.SousZoneOperator;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
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
import java.util.LinkedList;
import java.util.List;
@SpringBootTest(classes = PresenceSousZonesMemeZone.class)
public class PresenceSousZonesMemeZoneTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    private NoticeXml noticeXml;

    @BeforeEach
    void init() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        this.noticeXml = mapper.readValue(xml, NoticeXml.class);
    }

    @Test
    @DisplayName("test de la methode getZones")
    void getZones() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"200",sousZoneOperators);

        Assertions.assertEquals(1, presenceSousZonesMemeZone.getZones().size());
        Assertions.assertEquals("200$a", presenceSousZonesMemeZone.getZones().get(0));

        sousZoneOperators.add(new SousZoneOperator("b",true, BooleanOperateur.ET, null));
        Assertions.assertEquals(2, presenceSousZonesMemeZone.getZones().size());
        Assertions.assertEquals("200$a", presenceSousZonesMemeZone.getZones().get(0));
        Assertions.assertEquals("200$b", presenceSousZonesMemeZone.getZones().get(1));

        sousZoneOperators.add(new SousZoneOperator("c",false, BooleanOperateur.ET, null));
        Assertions.assertEquals(3, presenceSousZonesMemeZone.getZones().size());
        Assertions.assertEquals("200$a", presenceSousZonesMemeZone.getZones().get(0));
        Assertions.assertEquals("200$b", presenceSousZonesMemeZone.getZones().get(1));
        Assertions.assertEquals("200$c", presenceSousZonesMemeZone.getZones().get(2));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a ou d'une $c dans une zone 201 Sachant que la 201 n'existe pas")
    void isValid0() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true, null));
        sousZoneOperators.add(new SousZoneOperator("b",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"201",sousZoneOperators);

        Assertions.assertFalse(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a et d'une $b dans une zone 200")
    void isValid1() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true, null));
        sousZoneOperators.add(new SousZoneOperator("b",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"200",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a et absence d'une $b dans une zone 200 Sachant que la $b est bien presente")
    void isValid2() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true, null));
        sousZoneOperators.add(new SousZoneOperator("b",false,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"200",sousZoneOperators);

        Assertions.assertFalse(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a et d'une $c dans une zone 200 Sachant que la $c n'existe pas")
    void isValid3() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true, null));
        sousZoneOperators.add(new SousZoneOperator("c",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"200",sousZoneOperators);

        Assertions.assertFalse(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a ou d'une $c dans une zone 200 Sachant que la $c n'existe pas")
    void isValid4() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true, null));
        sousZoneOperators.add(new SousZoneOperator("c",true,BooleanOperateur.OU, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"200",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a ou absence d'une $e et precence d'une $b dans une zone 200")
    void isValid5() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true, null));
        sousZoneOperators.add(new SousZoneOperator("e",false,BooleanOperateur.OU, null));
        sousZoneOperators.add(new SousZoneOperator("b",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"200",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }


    @Test
    @DisplayName("test de la methode isValid precence d'une $6 et d'une $a dans une meme zone 181")
    void isValid6() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("6",true, null));
        sousZoneOperators.add(new SousZoneOperator("a",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"181",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $6 et d'une $a dans une meme zone 181")
    void isValid7() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("6",true, null));
        sousZoneOperators.add(new SousZoneOperator("c",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"181",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid absence d'une $7 et presence d'une $6 dans une meme zone 214")
    void isValid8() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("7",false, null));
        sousZoneOperators.add(new SousZoneOperator("6",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"214",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid presence d'une $7 et absence d'une $6 dans une meme zone 214")
    void isValid9() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("7",true, null));
        sousZoneOperators.add(new SousZoneOperator("6",false,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"214",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid absence d'une $7 et absence d'une $6 dans une meme zone 214")
    void isValid10() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("7",false, null));
        sousZoneOperators.add(new SousZoneOperator("6",false,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"214",sousZoneOperators);

        Assertions.assertTrue(presenceSousZonesMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid presence d'une $7 et presence d'une $6 dans une meme zone 214")
    void isValid11() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("7",true, null));
        sousZoneOperators.add(new SousZoneOperator("6",true,BooleanOperateur.ET, null));
        PresenceSousZonesMemeZone presenceSousZonesMemeZone = new PresenceSousZonesMemeZone(1,"214",sousZoneOperators);

        Assertions.assertFalse(presenceSousZonesMemeZone.isValid(noticeXml));
    }
}