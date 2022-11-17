package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.Priority;
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
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(2, "020", "a", true), BooleanOperateur.ET, 1, null));

        Assertions.assertTrue(complexRule.isValid(notice));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "021", "b", true), BooleanOperateur.ET, 1, null));
        Assertions.assertFalse(complexRule.isValid(notice));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "033", "a", true), BooleanOperateur.OU, 1, null));
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
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(2, "020", "a", true), BooleanOperateur.ET, 1, null));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "021", "b", true), BooleanOperateur.ET, 1, null));
        Assertions.assertFalse(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "033", "a", true), BooleanOperateur.OU, 1, null));
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
        complexRule.addOtherRule(new DependencyRule(1, "606", "3", 0, complexRule));

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
        complexRule.addOtherRule(new DependencyRule(1, "606", "3", 0, complexRule));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "152", "b", true), BooleanOperateur.ET, 1, complexRule));
        Assertions.assertTrue(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceZone(5, "153", true), BooleanOperateur.ET, 2, complexRule));
        Assertions.assertFalse(complexRule.isValid(noticeBiblio, noticeAutorite));
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


        complexRule.addOtherRule(new DependencyRule(1, "600", "a",3,complexRule));
        complexRule.addOtherRule(new LinkedRule(new PresenceZone(3, "400", false), BooleanOperateur.OU, 4, complexRule));
        Assertions.assertEquals(3, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("600$a", complexRule.getZonesFromChildren().get(2));
    }



}
