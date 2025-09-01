package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.Indicateur;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.PresenceChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance.Reciprocite;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PositionSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.positions.PositionsOperator;
import fr.abes.qualimarc.core.utils.*;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;

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

        ComplexRule complexRule = new ComplexRule(1, "Test", Priority.P1, new PresenceZone(1, "200", false, true));

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

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", false, true));
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(2, "020", false, "a", true), BooleanOperateur.ET, 1, complexRule));

        Assertions.assertTrue(complexRule.isValid(notice));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "021", false, "b", true), BooleanOperateur.ET, 1, complexRule));
        Assertions.assertFalse(complexRule.isValid(notice));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "033", false, "a", true), BooleanOperateur.OU, 1, complexRule));
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

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", false, true));
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(2, "020", false, "a", true), BooleanOperateur.ET, 1, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "021", false, "b", true), BooleanOperateur.ET, 1, complexRule));
        Assertions.assertFalse(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "033", false, "a", true), BooleanOperateur.OU, 1, complexRule));
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

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", false, true));
        complexRule.addOtherRule(new DependencyRule(1, "606", "3", TypeNoticeLiee.AUTORITE, 0, complexRule));

        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(4, "152", false, "b", true), BooleanOperateur.ET, 1, complexRule));
        Assertions.assertTrue(complexRule.isValid(noticeBiblio, noticeAutorite));

        complexRule.addOtherRule(new LinkedRule(new PresenceZone(5, "153", false, true), BooleanOperateur.ET, 2, complexRule));
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

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", false, true));
        complexRule.addOtherRule(new DependencyRule(1, "606", "3", TypeNoticeLiee.BIBLIO, 0, complexRule));
        complexRule.addOtherRule(new LinkedRule(new Reciprocite(4, "459", false, "0"), BooleanOperateur.ET, 1, complexRule));

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

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", false, "3", true));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "4", true), BooleanOperateur.ET, 0, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", false, "3", true));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "b", true), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertFalse(complexRule2.isValid(noticeBiblio));

        ComplexRule complexRule3 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", false, "3", true));
        complexRule3.setMemeZone(true);
        complexRule3.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "b", false), BooleanOperateur.ET, 0, complexRule3));

        Assertions.assertTrue(complexRule3.isValid(noticeBiblio));

        ComplexRule complexRule4 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", false, "b", false));
        complexRule4.setMemeZone(true);
        complexRule4.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "3", false), BooleanOperateur.ET, 0, complexRule4));

        Assertions.assertFalse(complexRule4.isValid(noticeBiblio));

        ComplexRule complexRule5 = new ComplexRule(1, "test", Priority.P1, new PresenceSousZone(1, "607", false, "b", false));
        complexRule5.setMemeZone(false);
        complexRule5.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "3", false), BooleanOperateur.ET, 0, complexRule5));

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

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "3", false), BooleanOperateur.ET, 0, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        ComplexRule complexRule1 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "608", false, false));
        complexRule1.setMemeZone(true);
        complexRule1.addOtherRule(new LinkedRule(new PresenceSousZone(3, "608", false, "3", false), BooleanOperateur.ET, 0, complexRule1));

        Assertions.assertFalse(complexRule1.isValid(noticeBiblio));

        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "y", true), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertFalse(complexRule2.isValid(noticeBiblio));

        ComplexRule complexRule3 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "608", false, false));
        complexRule3.setMemeZone(false);
        complexRule3.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "b", true), BooleanOperateur.ET, 0, complexRule3));

        Assertions.assertTrue(complexRule3.isValid(noticeBiblio));

        ComplexRule complexRule4 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule4.setMemeZone(true);
        complexRule4.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "b", false), BooleanOperateur.ET, 0, complexRule4));

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
        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "214", false, true));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new Indicateur(3, "214", false, 1, "0", TypeVerification.STRICTEMENT), BooleanOperateur.ET, 0, complexRule));
        complexRule.addOtherRule(new LinkedRule(new Indicateur(4, "214", false, 2, "#", TypeVerification.STRICTEMENT), BooleanOperateur.ET, 1, complexRule));
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(5, "214", false, "d", true), BooleanOperateur.ET, 2, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        // 607 présente ET 607 ind 2 qui contient "1"
        ComplexRule complexRule1 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule1.setMemeZone(true);
        complexRule1.addOtherRule(new LinkedRule(new Indicateur(3, "607", false, 2, "1", TypeVerification.STRICTEMENT), BooleanOperateur.ET, 0, complexRule1));

        Assertions.assertTrue(complexRule1.isValid(noticeBiblio));

        // 608 présente ET 608 ind 2 qui contient "1"
        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "608", false, true));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new Indicateur(3, "608", false, 2, "1", TypeVerification.STRICTEMENT), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertFalse(complexRule2.isValid(noticeBiblio));

        // 607 présente ET 607 ind 2 qui contient "1" ET 607 ind 1 qui contient "0"
        ComplexRule complexRule3 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule3.setMemeZone(true);
        complexRule3.addOtherRule(new LinkedRule(new Indicateur(3, "607", false, 2, "1", TypeVerification.STRICTEMENT), BooleanOperateur.ET, 0, complexRule3));
        complexRule3.addOtherRule(new LinkedRule(new Indicateur(4, "607", false, 1, "0", TypeVerification.STRICTEMENT), BooleanOperateur.ET, 1, complexRule3));

        Assertions.assertFalse(complexRule3.isValid(noticeBiblio));
    }

    @Test
    @DisplayName("Test même zone PresenceChaineCaracteres")
    void testMemeZonePresenceChaineCaracteres() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml noticeBiblio = mapper.readValue(xml, NoticeXml.class);

        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------
        //  --------------------------     STRICTEMENT     --------------------------------
        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------

        //  STRICTEMENT / PAS DE BOOLEAN / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres = new TreeSet<>();
        listChaineCaracteres.add(new ChaineCaracteres(1, "Informatique", null));

        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule.setMemeZone(true);
        complexRule.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607", false, "a", TypeVerification.STRICTEMENT, listChaineCaracteres), BooleanOperateur.ET, 1, complexRule));
        complexRule.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        //  STRICTEMENT / PAS DE BOOLEAN / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres1 = new TreeSet<>();
        listChaineCaracteres1.add(new ChaineCaracteres(1, "Informatiqu", null));

        ComplexRule complexRule1 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule1.setMemeZone(true);
        complexRule1.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.STRICTEMENT, listChaineCaracteres1), BooleanOperateur.ET, 1, complexRule1));
        complexRule1.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule1));

        Assertions.assertFalse(complexRule1.isValid(noticeBiblio));

        //  STRICTEMENT / OU / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres2 = new TreeSet<>();
        listChaineCaracteres2.add(new ChaineCaracteres(1, "Inform", null));
        listChaineCaracteres2.add(new ChaineCaracteres(2, BooleanOperateur.OU, "Informatique", null));

        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule2.setMemeZone(true);
        complexRule2.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.STRICTEMENT, listChaineCaracteres2), BooleanOperateur.ET, 1, complexRule2));
        complexRule2.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertTrue(complexRule2.isValid(noticeBiblio));

        //  STRICTEMENT / OU / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres2b = new TreeSet<>();
        listChaineCaracteres2b.add(new ChaineCaracteres(1, "Inform", null));
        listChaineCaracteres2b.add(new ChaineCaracteres(2, BooleanOperateur.OU, "atique", null));

        ComplexRule complexRule2b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607",  false,true));
        complexRule2b.setMemeZone(true);
        complexRule2b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.STRICTEMENT, listChaineCaracteres2b), BooleanOperateur.ET, 1, complexRule2b));
        complexRule2b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule2b));

        Assertions.assertFalse(complexRule2b.isValid(noticeBiblio));

        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------
        //  ----------------------------     COMMENCE     ---------------------------------
        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------

        //  COMMENCE / PAS DE BOOLEAN / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres3 = new TreeSet<>();
        listChaineCaracteres3.add(new ChaineCaracteres(1, "Info", null));

        ComplexRule complexRule3 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule3.setMemeZone(true);
        complexRule3.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.COMMENCE, listChaineCaracteres3), BooleanOperateur.ET, 1, complexRule3));
        complexRule3.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule3));

        Assertions.assertTrue(complexRule3.isValid(noticeBiblio));

        //  COMMENCE / PAS DE BOOLEAN / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres4 = new TreeSet<>();
        listChaineCaracteres4.add(new ChaineCaracteres(1, "nfo", null));

        ComplexRule complexRule4 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule4.setMemeZone(true);
        complexRule4.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.COMMENCE, listChaineCaracteres4), BooleanOperateur.ET, 1, complexRule4));
        complexRule4.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule4));

        Assertions.assertFalse(complexRule4.isValid(noticeBiblio));

        //  COMMENCE / OU / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres5 = new TreeSet<>();
        listChaineCaracteres5.add(new ChaineCaracteres(1, "nfo", null));
        listChaineCaracteres5.add(new ChaineCaracteres(2, BooleanOperateur.OU, "Info", null));

        ComplexRule complexRule5 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule5.setMemeZone(true);
        complexRule5.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.COMMENCE, listChaineCaracteres5), BooleanOperateur.ET, 1, complexRule5));
        complexRule5.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule5));

        Assertions.assertTrue(complexRule5.isValid(noticeBiblio));

        //  COMMENCE / OU / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres5b = new TreeSet<>();
        listChaineCaracteres5b.add(new ChaineCaracteres(1, "nfo", null));
        listChaineCaracteres5b.add(new ChaineCaracteres(2, BooleanOperateur.OU, "for", null));

        ComplexRule complexRule5b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule5b.setMemeZone(true);
        complexRule5b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.COMMENCE, listChaineCaracteres5b), BooleanOperateur.ET, 1, complexRule5b));
        complexRule5b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607",  false, "5", false), BooleanOperateur.ET, 0, complexRule5b));

        Assertions.assertFalse(complexRule5b.isValid(noticeBiblio));

        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------
        //  -----------------------------     TERMINE     ---------------------------------
        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------

        //  TERMINE / PAS DE BOOLEAN / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres6 = new TreeSet<>();
        listChaineCaracteres6.add(new ChaineCaracteres(1, "matique", null));

        ComplexRule complexRule6 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule6.setMemeZone(true);
        complexRule6.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.TERMINE, listChaineCaracteres6), BooleanOperateur.ET, 1, complexRule6));
        complexRule6.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false, complexRule6), BooleanOperateur.ET, 0, complexRule6));

        Assertions.assertTrue(complexRule6.isValid(noticeBiblio));

        //  TERMINE / PAS DE BOOLEAN / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres6b = new TreeSet<>();
        listChaineCaracteres6b.add(new ChaineCaracteres(1, "mat", null));

        ComplexRule complexRule6b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule6b.setMemeZone(true);
        complexRule6b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.TERMINE, listChaineCaracteres6b), BooleanOperateur.ET, 1, complexRule6b));
        complexRule6b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607",  false, "5", false), BooleanOperateur.ET, 0, complexRule6b));

        Assertions.assertFalse(complexRule6b.isValid(noticeBiblio));

        //  TERMINE / OU / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres7 = new TreeSet<>();
        listChaineCaracteres7.add(new ChaineCaracteres(1, "mat", null));
        listChaineCaracteres7.add(new ChaineCaracteres(2, BooleanOperateur.OU, "matique", null));

        ComplexRule complexRule7 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule7.setMemeZone(true);
        complexRule7.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.TERMINE, listChaineCaracteres7), BooleanOperateur.ET, 1, complexRule7));
        complexRule7.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule7));

        Assertions.assertTrue(complexRule7.isValid(noticeBiblio));

        //  TERMINE / OU / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres7b = new TreeSet<>();
        listChaineCaracteres7b.add(new ChaineCaracteres(1, "mat", null));
        listChaineCaracteres7b.add(new ChaineCaracteres(2, BooleanOperateur.OU, "Info", null));

        ComplexRule complexRule7b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule7b.setMemeZone(true);
        complexRule7b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.TERMINE, listChaineCaracteres7b), BooleanOperateur.ET, 1, complexRule7b));
        complexRule7b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule7b));

        Assertions.assertFalse(complexRule7b.isValid(noticeBiblio));

        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------
        //  -----------------------------     CONTIENT     --------------------------------
        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------

        //  CONTIENT / PAS DE BOOLEAN / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres8 = new TreeSet<>();
        listChaineCaracteres8.add(new ChaineCaracteres(1, "format", null));

        ComplexRule complexRule8 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule8.setMemeZone(true);
        complexRule8.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.CONTIENT, listChaineCaracteres8), BooleanOperateur.ET, 1, complexRule8));
        complexRule8.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule8));

        Assertions.assertTrue(complexRule8.isValid(noticeBiblio));

        //  CONTIENT / PAS DE BOOLEAN / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres8b = new TreeSet<>();
        listChaineCaracteres8b.add(new ChaineCaracteres(1, "formation", null));

        ComplexRule complexRule8b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule8b.setMemeZone(true);
        complexRule8b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.CONTIENT, listChaineCaracteres8b), BooleanOperateur.ET, 1, complexRule8b));
        complexRule8b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule8b));

        Assertions.assertFalse(complexRule8b.isValid(noticeBiblio));

        //  CONTIENT / OU / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres9 = new TreeSet<>();
        listChaineCaracteres9.add(new ChaineCaracteres(1, "formation", null));
        listChaineCaracteres9.add(new ChaineCaracteres(2, BooleanOperateur.OU, "format", null));

        ComplexRule complexRule9 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule9.setMemeZone(true);
        complexRule9.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607", false,"a", TypeVerification.CONTIENT, listChaineCaracteres9), BooleanOperateur.ET, 1, complexRule9));
        complexRule9.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule9));

        Assertions.assertTrue(complexRule7.isValid(noticeBiblio));

        //  CONTIENT / OU / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres9b = new TreeSet<>();
        listChaineCaracteres9b.add(new ChaineCaracteres(1, "formation", null));
        listChaineCaracteres9b.add(new ChaineCaracteres(2, BooleanOperateur.OU, "formant", null));

        ComplexRule complexRule9b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule9b.setMemeZone(true);
        complexRule9b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.CONTIENT, listChaineCaracteres9b), BooleanOperateur.ET, 1, complexRule9b));
        complexRule9b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule9b));

        Assertions.assertFalse(complexRule9b.isValid(noticeBiblio));

        //  CONTIENT / ET / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres10 = new TreeSet<>();
        listChaineCaracteres10.add(new ChaineCaracteres(1, "Infor", null));
        listChaineCaracteres10.add(new ChaineCaracteres(2, BooleanOperateur.ET, "matique", null));

        ComplexRule complexRule10 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule10.setMemeZone(true);
        complexRule10.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false,"a", TypeVerification.CONTIENT, listChaineCaracteres10), BooleanOperateur.ET, 1, complexRule10));
        complexRule10.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule10));

        Assertions.assertTrue(complexRule10.isValid(noticeBiblio));

        //  CONTIENT / ET / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres10b = new TreeSet<>();
        listChaineCaracteres10b.add(new ChaineCaracteres(1, "Infaur", null));
        listChaineCaracteres10b.add(new ChaineCaracteres(2, BooleanOperateur.ET, "matik", null));

        ComplexRule complexRule10b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule10b.setMemeZone(true);
        complexRule10b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.CONTIENT, listChaineCaracteres10b), BooleanOperateur.ET, 1, complexRule10b));
        complexRule10b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607",  false,"5", false), BooleanOperateur.ET, 0, complexRule10b));

        Assertions.assertFalse(complexRule10b.isValid(noticeBiblio));

        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------
        //  ---------------------------     NECONTIENTPAS     -----------------------------
        //  -------------------------------------------------------------------------------
        //  -------------------------------------------------------------------------------

        //  NECONTIENTPAS / PAS DE BOOLEAN / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres11 = new TreeSet<>();
        listChaineCaracteres11.add(new ChaineCaracteres(1, "NotFound", null));

        ComplexRule complexRule11 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule11.setMemeZone(true);
        complexRule11.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.NECONTIENTPAS, listChaineCaracteres11), BooleanOperateur.ET, 1, complexRule11));
        complexRule11.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule11));

        Assertions.assertTrue(complexRule11.isValid(noticeBiblio));

        //  NECONTIENTPAS / PAS DE BOOLEAN / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres11b = new TreeSet<>();
        listChaineCaracteres11b.add(new ChaineCaracteres(1, "Informatique", null));

        ComplexRule complexRule11b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule11b.setMemeZone(true);
        complexRule11b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.NECONTIENTPAS, listChaineCaracteres11b), BooleanOperateur.ET, 1, complexRule11b));
        complexRule11b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule11b));

        Assertions.assertFalse(complexRule11b.isValid(noticeBiblio));

        //  NECONTIENTPAS / OU / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres12 = new TreeSet<>();
        listChaineCaracteres12.add(new ChaineCaracteres(1, "NotFound", null));
        listChaineCaracteres12.add(new ChaineCaracteres(2, BooleanOperateur.OU, "NotFound2", null));

        ComplexRule complexRule12 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule12.setMemeZone(true);
        complexRule12.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607", false,"a", TypeVerification.NECONTIENTPAS, listChaineCaracteres12), BooleanOperateur.ET, 1, complexRule12));
        complexRule12.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule12));

        Assertions.assertTrue(complexRule12.isValid(noticeBiblio));

        //  NECONTIENTPAS / OU / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres12b = new TreeSet<>();
        listChaineCaracteres12b.add(new ChaineCaracteres(1, "Infor", null));
        listChaineCaracteres12b.add(new ChaineCaracteres(2, BooleanOperateur.OU, "matique", null));

        ComplexRule complexRule12b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule12b.setMemeZone(true);
        complexRule12b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.NECONTIENTPAS, listChaineCaracteres12b), BooleanOperateur.ET, 1, complexRule12b));
        complexRule12b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule12b));

        Assertions.assertFalse(complexRule12b.isValid(noticeBiblio));

        //  NECONTIENTPAS / ET / RESULT TRUE
        TreeSet<ChaineCaracteres> listChaineCaracteres13 = new TreeSet<>();
        listChaineCaracteres13.add(new ChaineCaracteres(1, "Not", null));
        listChaineCaracteres13.add(new ChaineCaracteres(2, BooleanOperateur.ET, "Found", null));

        ComplexRule complexRule13 = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule13.setMemeZone(true);
        complexRule13.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607", false,"a", TypeVerification.NECONTIENTPAS, listChaineCaracteres13), BooleanOperateur.ET, 1, complexRule13));
        complexRule13.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule13));

        Assertions.assertTrue(complexRule13.isValid(noticeBiblio));

        //  NECONTIENTPAS / ET / RESULT FALSE
        TreeSet<ChaineCaracteres> listChaineCaracteres13b = new TreeSet<>();
        listChaineCaracteres13b.add(new ChaineCaracteres(1, "Infor", null));
        listChaineCaracteres13b.add(new ChaineCaracteres(2, BooleanOperateur.ET, "matique", null));

        ComplexRule complexRule13b = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "607", false, true));
        complexRule13b.setMemeZone(true);
        complexRule13b.addOtherRule(new LinkedRule(new PresenceChaineCaracteres(1, "607",false, "a", TypeVerification.NECONTIENTPAS, listChaineCaracteres13b), BooleanOperateur.ET, 1, complexRule13b));
        complexRule13b.addOtherRule(new LinkedRule(new PresenceSousZone(3, "607", false, "5", false), BooleanOperateur.ET, 0, complexRule13b));

        Assertions.assertFalse(complexRule13b.isValid(noticeBiblio));
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
        PositionsOperator positionsOperator = new PositionsOperator(1, 1, ComparaisonOperateur.EGAL);
        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PositionSousZone(1, "607", false, "4", Lists.newArrayList(positionsOperator), BooleanOperateur.OU));
        complexRule.setMemeZone(true);
        positionsOperator = new PositionsOperator(1, 3, ComparaisonOperateur.EGAL);
        complexRule.addOtherRule(new LinkedRule(new PositionSousZone(3, "607", false, "b", Lists.newArrayList(positionsOperator), BooleanOperateur.OU), BooleanOperateur.ET, 0, complexRule));

        Assertions.assertTrue(complexRule.isValid(noticeBiblio));

        // la zone 607 avec la sous zone 4 en position 2 ET la sous zone b en position 3
        positionsOperator = new PositionsOperator(1, 2, ComparaisonOperateur.EGAL);
        ComplexRule complexRule1 = new ComplexRule(1, "test", Priority.P1, new PositionSousZone(1, "607",  false,"4", Lists.newArrayList(positionsOperator), BooleanOperateur.OU));
        complexRule1.setMemeZone(true);
        positionsOperator = new PositionsOperator(1, 3, ComparaisonOperateur.EGAL);
        complexRule1.addOtherRule(new LinkedRule(new PositionSousZone(3, "607",  false,"b", Lists.newArrayList(positionsOperator), BooleanOperateur.OU), BooleanOperateur.ET, 0, complexRule1));

        Assertions.assertFalse(complexRule1.isValid(noticeBiblio));

        // la zone 607 avec la sous zone 4 en position 4 ET la sous zone 3 en position 1
        positionsOperator = new PositionsOperator(1, 4, ComparaisonOperateur.EGAL);
        ComplexRule complexRule2 = new ComplexRule(1, "test", Priority.P1, new PositionSousZone(1, "607", false, "4", Lists.newArrayList(positionsOperator), BooleanOperateur.OU));
        complexRule2.setMemeZone(true);
        positionsOperator = new PositionsOperator(1, 1, ComparaisonOperateur.EGAL);
        complexRule2.addOtherRule(new LinkedRule(new PositionSousZone(3, "607", false, "3", Lists.newArrayList(positionsOperator), BooleanOperateur.OU), BooleanOperateur.ET, 0, complexRule2));

        Assertions.assertTrue(complexRule2.isValid(noticeBiblio));
    }

        @Test
    @DisplayName("test getZonesFromChildren")
    void testGetZonesFromChildren() {
        ComplexRule complexRule = new ComplexRule(1, "test", Priority.P1, new PresenceZone(1, "200", false, true));

        Assertions.assertEquals(1, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("200", complexRule.getZonesFromChildren().get(0));

        complexRule.addOtherRule(new LinkedRule(new PresenceZone(1, "300", false, false), BooleanOperateur.OU, 1, complexRule));
        Assertions.assertEquals(2, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("300", complexRule.getZonesFromChildren().get(1));

        complexRule.addOtherRule(new LinkedRule(new PresenceZone(2, "300", false, false), BooleanOperateur.OU, 2, complexRule));
        Assertions.assertEquals(2, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("300", complexRule.getZonesFromChildren().get(1));


        complexRule.addOtherRule(new DependencyRule(1, "600", "a", TypeNoticeLiee.AUTORITE,3,complexRule));
        complexRule.addOtherRule(new LinkedRule(new PresenceZone(3, "400",  false, false), BooleanOperateur.OU, 4, complexRule));
        Assertions.assertEquals(3, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals("600$a", complexRule.getZonesFromChildren().get(2));
    }



}
