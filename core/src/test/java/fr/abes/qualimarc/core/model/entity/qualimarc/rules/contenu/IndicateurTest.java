package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
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

@SpringBootTest(classes = {Indicateur.class})
public class IndicateurTest {
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
    @DisplayName("test indicateur 1")
    void isValid1() {
        Indicateur rule1 = new Indicateur(1,"101",1,"0", TypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule1.isValid(notice));

        Indicateur rule2 = new Indicateur(1,"101",1,"#", TypeVerification.STRICTEMENT);
        Assertions.assertFalse(rule2.isValid(notice));

        Indicateur rule3= new Indicateur(1,"073",1,"#", TypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule3.isValid(notice));
    }
    @Test
    @DisplayName("test indicateur 2")
    void isValid2() {
        Indicateur rule1 = new Indicateur(1,"073",2,"0", TypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule1.isValid(notice));

        Indicateur rule2 = new Indicateur(1,"073",2,"#", TypeVerification.STRICTEMENT);
        Assertions.assertFalse(rule2.isValid(notice));

        Indicateur rule3= new Indicateur(1,"101",2,"#", TypeVerification.STRICTEMENT);
        Assertions.assertTrue(rule3.isValid(notice));
    }

    @Test
    @DisplayName("test indicateur 1 STRICTEMENTDIFFERENT")
    void isValid3() {
        Indicateur rule1 = new Indicateur(1,"101",1,"0", TypeVerification.STRICTEMENTDIFFERENT);
        Assertions.assertFalse(rule1.isValid(notice));

        Indicateur rule2 = new Indicateur(1,"101",1,"#", TypeVerification.STRICTEMENTDIFFERENT);
        Assertions.assertTrue(rule2.isValid(notice));

        Indicateur rule3= new Indicateur(1,"073",1,"#", TypeVerification.STRICTEMENTDIFFERENT);
        Assertions.assertFalse(rule3.isValid(notice));
    }

    @Test
    @DisplayName("test indicateur 2 STRICTEMENTDIFFERENT")
    void isValid4() {
        Indicateur rule1 = new Indicateur(1,"073",2,"0", TypeVerification.STRICTEMENTDIFFERENT);
        Assertions.assertFalse(rule1.isValid(notice));

        Indicateur rule2 = new Indicateur(1,"073",2,"#", TypeVerification.STRICTEMENTDIFFERENT);
        Assertions.assertTrue(rule2.isValid(notice));

        Indicateur rule3= new Indicateur(1,"101",2,"#", TypeVerification.STRICTEMENTDIFFERENT);
        Assertions.assertFalse(rule3.isValid(notice));
    }

    @Test
    void testGetZones() {
        Indicateur rule = new Indicateur(1, "100", 1, "#", TypeVerification.STRICTEMENT);
        Assertions.assertEquals(1, rule.getZones().size());
        Assertions.assertEquals("100", rule.getZones().get(0));
    }


}