package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
@SpringBootTest(classes = PresenceSousZoneMemeZone.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class PresenceSousZoneMemeZoneTest {

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
        sousZoneOperators.add(new SousZoneOperator("a",true));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"200",sousZoneOperators);

        Assertions.assertEquals("200$a",presenceSousZoneMemeZone.getZones());

        sousZoneOperators.add(new SousZoneOperator("b",true, BooleanOperateur.ET));
        Assertions.assertEquals("200$a/200$b",presenceSousZoneMemeZone.getZones());

        sousZoneOperators.add(new SousZoneOperator("c",false, BooleanOperateur.ET));
        Assertions.assertEquals("200$a/200$b/200$c",presenceSousZoneMemeZone.getZones());
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a ou d'une $c dans une zone 201 Sachant que la 201 n'existe pas")
    void isValid0() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true));
        sousZoneOperators.add(new SousZoneOperator("b",true,BooleanOperateur.ET));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"201",sousZoneOperators);

        Assertions.assertFalse(presenceSousZoneMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a et d'une $b dans une zone 200")
    void isValid1() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true));
        sousZoneOperators.add(new SousZoneOperator("b",true,BooleanOperateur.ET));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"200",sousZoneOperators);

        Assertions.assertTrue(presenceSousZoneMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a et absence d'une $b dans une zone 200 Sachant que la $b est bien presente")
    void isValid2() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true));
        sousZoneOperators.add(new SousZoneOperator("b",false,BooleanOperateur.ET));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"200",sousZoneOperators);

        Assertions.assertFalse(presenceSousZoneMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a et d'une $c dans une zone 200 Sachant que la $c n'existe pas")
    void isValid3() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true));
        sousZoneOperators.add(new SousZoneOperator("c",true,BooleanOperateur.ET));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"200",sousZoneOperators);

        Assertions.assertFalse(presenceSousZoneMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a ou d'une $c dans une zone 200 Sachant que la $c n'existe pas")
    void isValid4() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true));
        sousZoneOperators.add(new SousZoneOperator("c",true,BooleanOperateur.OU));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"200",sousZoneOperators);

        Assertions.assertTrue(presenceSousZoneMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $a ou absence d'une $e et precence d'une $b dans une zone 200")
    void isValid5() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("a",true));
        sousZoneOperators.add(new SousZoneOperator("e",false,BooleanOperateur.OU));
        sousZoneOperators.add(new SousZoneOperator("b",true,BooleanOperateur.ET));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"200",sousZoneOperators);

        Assertions.assertTrue(presenceSousZoneMemeZone.isValid(noticeXml));
    }


    @Test
    @DisplayName("test de la methode isValid precence d'une $6 et d'une $a dans une meme zone 181")
    void isValid6() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("6",true));
        sousZoneOperators.add(new SousZoneOperator("a",true,BooleanOperateur.ET));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"181",sousZoneOperators);

        Assertions.assertTrue(presenceSousZoneMemeZone.isValid(noticeXml));
    }

    @Test
    @DisplayName("test de la methode isValid precence d'une $6 et d'une $a dans une meme zone 181")
    void isValid7() {
        List<SousZoneOperator> sousZoneOperators = new LinkedList<>();
        sousZoneOperators.add(new SousZoneOperator("6",true));
        sousZoneOperators.add(new SousZoneOperator("c",true,BooleanOperateur.ET));
        PresenceSousZoneMemeZone presenceSousZoneMemeZone = new PresenceSousZoneMemeZone(1,"181",sousZoneOperators);

        Assertions.assertTrue(presenceSousZoneMemeZone.isValid(noticeXml));
    }
}