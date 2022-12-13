package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.Indicateur;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance.Reciprocite;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PositionSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeNoticeLiee;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {ComplexRule.class})
public class ComplexRuleTest {
    @Value("classpath:143519379.xml")
    private Resource xmlFileNoticeBiblio;

    @Value("classpath:02787088X.xml")
    private Resource xmlFileNoticeAutorite;

    @Test
    @DisplayName("test méthode isValid règle complexe avec une seule règle simple")
    void isValidWithOneRule() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        ComplexRule complexRule = new ComplexRule(1, "Test", Priority.P1, new PresenceZone(1, "200", true));

        Assertions.assertTrue(complexRule.isValid(notice));
    }

    @Test
    @DisplayName("test méthode isValid règle complexe avec plusieurs règles simples")
    void isValidWithSeveralRules() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", true));
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(2, "020", "a", true), BooleanOperateur.ET, 1, complexRule));

        Assertions.assertTrue(complexRule.isValid(notice));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "021", "b", true), BooleanOperateur.ET, 1, complexRule));
        Assertions.assertFalse(complexRule.isValid(notice));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "033", "a", true), BooleanOperateur.OU, 1, complexRule));
        Assertions.assertTrue(complexRule.isValid(notice));
    }

    @Test
    @DisplayName("test méthode isValid règle complexe avec plusieurs notices, et sans règle de dépendance")
    void isValidTwoNoticesWithoutDependency() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        String xml2 = IOUtils.toString(new FileInputStream(xmlFileNoticeAutorite.getFile()), StandardCharsets.UTF_8);
        NoticeXml noticeAutorite = mapper.readValue(xml2, NoticeXml.class);

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", true));
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(2, "020", "a", true), BooleanOperateur.ET, 1, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "021", "b", true), BooleanOperateur.ET, 1, complexRule));
        Assertions.assertFalse(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "033", "a", true), BooleanOperateur.OU, 1, complexRule));
        Assertions.assertTrue(complexRule.isValid(noticeBiblio, noticeAutorite));
    }

    @Test
    @DisplayName("test méthode isValid règle complexe avec plusieurs notices, et avec règle de dépendance")
    void isValidTwoNoticesWithDependency() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        String xml2 = IOUtils.toString(new FileInputStream(xmlFileNoticeAutorite.getFile()), StandardCharsets.UTF_8);
        NoticeXml noticeAutorite = mapper.readValue(xml2, NoticeXml.class);

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", true));
        complexRule.addOtherRule(new DependencyRule(1, "606", "3", TypeNoticeLiee.AUTORITE, 0, complexRule));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "152", "b", true), BooleanOperateur.ET, 1, complexRule));
        Assertions.assertTrue(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceZone(5, "153", true), BooleanOperateur.ET, 2, complexRule));
        Assertions.assertFalse(complexRule.isValid(noticeBiblio, noticeAutorite));
    }

    @Test
    @DisplayName("test méthode isValid règle complexe avec plusieurs notices, et avec règle de dépendance et une réciprocité")
    void isValidTwoNoticesWithDependencyAndReciprocite() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        String xml2 = IOUtils.toString(new FileInputStream(xmlFileNoticeAutorite.getFile()), StandardCharsets.UTF_8);
        NoticeXml noticeAutorite = mapper.readValue(xml2, NoticeXml.class);

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", true));
        complexRule.addOtherRule(new DependencyRule(1, "606", "3", TypeNoticeLiee.BIBLIO, 0, complexRule));
        complexRule.addOtherRule(new LinkedRule(new Reciprocite(4, "459", "0"), BooleanOperateur.ET, 1, complexRule));

        Assertions.assertFalse(complexRule.isValid(noticeBiblio, noticeAutorite));
    }

    @Test
    @DisplayName("test isValid même instance de zone")
    void testIsValidOneNoticeMemeZone() throws IOException {

        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", "3", true));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "4", true), BooleanOperateur.ET, 0, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", "3", true));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "b", true), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertFalse(complexRule2.isValid(noticeBiblio));

        ComplexRule complexRule3 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", "3", true));
        complexRule3.setMemeZone(true);
        complexRule3.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "b", false), BooleanOperateur.ET, 0, complexRule3));

        Assertions.assertTrue(complexRule3.isValid(noticeBiblio));

        ComplexRule complexRule4 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", "b", false));
        complexRule4.setMemeZone(true);
        complexRule4.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "3", false), BooleanOperateur.ET, 0, complexRule4));

        Assertions.assertFalse(complexRule4.isValid(noticeBiblio));

        ComplexRule complexRule5 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", "b", false));
        complexRule5.setMemeZone(false);
        complexRule5.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "3", false), BooleanOperateur.ET, 0, complexRule5));

        Assertions.assertFalse(complexRule5.isValid(noticeBiblio));
    }

    @Test
    @DisplayName("Test même zone PresenceZone")
    void testMemeZonePresenceZone() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", true));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "3", false), BooleanOperateur.ET, 0, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        ComplexRule complexRule1 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "608", false));
        complexRule1.setMemeZone(true);
        complexRule1.addOtherRule(new LinkedRule(new PresenceSousZone(3, "608", "3", false), BooleanOperateur.ET, 0, complexRule1));

        Assertions.assertFalse(complexRule1.isValid(noticeBiblio));

        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", true));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "y", true), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertFalse(complexRule2.isValid(noticeBiblio));

        ComplexRule complexRule3 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "608", false));
        complexRule3.setMemeZone(false);
        complexRule3.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "b", true), BooleanOperateur.ET, 0, complexRule3));

        Assertions.assertTrue(complexRule3.isValid(noticeBiblio));

        ComplexRule complexRule4 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", true));
        complexRule4.setMemeZone(true);
        complexRule4.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", "b", false), BooleanOperateur.ET, 0, complexRule4));

        Assertions.assertTrue(complexRule4.isValid(noticeBiblio));
    }

    @Test
    @DisplayName("Test même zone Indicateur")
    void testMemeZoneIndicateur() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        // 214 présente ET 214 ind 1 qui contient "0" ET 214 ind2 qui contient "#" ET une 214$d ct pour rigolé
        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "214", true));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new Indicateur(3, "214", 1, "0"), BooleanOperateur.ET, 0, complexRule));
        complexRule.addOtherRule(new LinkedRule(new Indicateur(4, "214", 2, "#"), BooleanOperateur.ET, 1, complexRule));
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(5, "214", "d", true), BooleanOperateur.ET, 2, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        // 607 présente ET 607 ind 2 qui contient "1"
        ComplexRule complexRule1 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", true));
        complexRule1.setMemeZone(true);
        complexRule1.addOtherRule(new LinkedRule(new Indicateur(3, "607", 2, "1"), BooleanOperateur.ET, 0, complexRule1));

        Assertions.assertTrue(complexRule1.isValid(noticeBiblio));

        // 608 présente ET 608 ind 2 qui contient "1"
        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "608", true));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new Indicateur(3, "608", 2, "1"), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertFalse(complexRule2.isValid(noticeBiblio));

        // 607 présente ET 607 ind 2 qui contient "1" ET 607 ind 1 qui contient "0"
        ComplexRule complexRule3 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", true));
        complexRule3.setMemeZone(true);
        complexRule3.addOtherRule(new LinkedRule(new Indicateur(3, "607", 2, "1"), BooleanOperateur.ET, 0, complexRule3));
        complexRule3.addOtherRule(new LinkedRule(new Indicateur(4, "607", 1, "0"), BooleanOperateur.ET, 1, complexRule3));

        Assertions.assertFalse(complexRule3.isValid(noticeBiblio));
    }

    @Test
    @DisplayName("Test même zone Indicateur")
    void testMemeZonePositionSousZone() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        // la zone 607 avec la sous zone 4 en position 1 ET la sous zone b en position 3
        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PositionSousZone(1, "607", "4", 1));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new PositionSousZone(3, "607", "b", 3), BooleanOperateur.ET, 0, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        // la zone 607 avec la sous zone 4 en position 2 ET la sous zone b en position 3
        ComplexRule complexRule1 = new ComplexRule(1, "test", Priority.P1, new PositionSousZone(1, "607", "4", 2));
        complexRule1.setMemeZone(true);
        complexRule1.addOtherRule(new LinkedRule(new PositionSousZone(3, "607", "b", 3), BooleanOperateur.ET, 0, complexRule1));

        Assertions.assertFalse(complexRule1.isValid(noticeBiblio));

        // la zone 607 avec la sous zone 4 en position 4 ET la sous zone 3 en position 1
        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PositionSousZone(1, "607", "4", 4));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new PositionSousZone(3, "607", "3", 1), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertTrue(complexRule2.isValid(noticeBiblio));
    }

        @Test
    @DisplayName("test getZonesFromChildren")
    void testGetZonesFromChildren() {
        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", true));

        Assertions.assertEquals(1, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("200", complexRule.getZonesFromChildren().get(0));

        complexRule.addOtherRule(new LinkedRule(new PresenceZone(1, "300", false), BooleanOperateur.OU, 1, complexRule));
        Assertions.assertEquals(2, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("300", complexRule.getZonesFromChildren().get(1));

        complexRule.addOtherRule(new LinkedRule(new PresenceZone(2, "300", false), BooleanOperateur.OU, 2, complexRule));
        Assertions.assertEquals(2, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("300", complexRule.getZonesFromChildren().get(1));


        complexRule.addOtherRule(new DependencyRule(1, "600", "a", TypeNoticeLiee.AUTORITE,3,complexRule));
        complexRule.addOtherRule(new LinkedRule(new PresenceZone(3, "400", false), BooleanOperateur.OU, 4, complexRule));
        Assertions.assertEquals(3, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("600$a", complexRule.getZonesFromChildren().get(2));
    }



}
