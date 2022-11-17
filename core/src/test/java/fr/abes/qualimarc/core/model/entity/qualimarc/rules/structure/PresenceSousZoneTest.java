package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
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

@SpringBootTest(classes = {PresenceSousZone.class})
public class PresenceSousZoneTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice1;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        //la zone n'existe pas dans la notice, donc la sous zone n'est pas présente donc le test est faux
        SimpleRule rule0 = new PresenceSousZone(1, "190", "a", true);
        Assertions.assertFalse(rule0.isValid(notice));

        SimpleRule rule1 = new PresenceSousZone(1, "010", "a", true);
        Assertions.assertTrue(rule1.isValid(notice));

        SimpleRule rule2 = new PresenceSousZone(2, "010", "j", true);
        Assertions.assertFalse(rule2.isValid(notice));

        SimpleRule rule3 = new PresenceSousZone(3, "020", "a", false);
        Assertions.assertFalse(rule3.isValid(notice));

        SimpleRule rule4 = new PresenceSousZone(4, "020", "j", false);
        Assertions.assertTrue(rule4.isValid(notice));
    }

    @Test
    @DisplayName("test getZones")
    void getZones() {
        PresenceSousZone rule = new PresenceSousZone(1, "020", "a", true);
        Assertions.assertEquals("020$a", rule.getZones());
    }
}