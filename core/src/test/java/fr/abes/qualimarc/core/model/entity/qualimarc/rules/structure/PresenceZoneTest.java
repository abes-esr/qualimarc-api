package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {PresenceZone.class})
public class PresenceZoneTest {
    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        SimpleRule rule = new PresenceZone(1, "010",false, true);
        Assertions.assertTrue(rule.isValid(notice));

        SimpleRule rule2 = new PresenceZone(2, "011",false, false);
        Assertions.assertTrue(rule2.isValid(notice));

        SimpleRule rule3 = new PresenceZone(3, "011",false, true);
        Assertions.assertFalse(rule3.isValid(notice));

        SimpleRule rule4 = new PresenceZone(4, "010",false, false);
        Assertions.assertFalse(rule4.isValid(notice));
    }

    @Test
    void testGetZones() {
        SimpleRule rule = new PresenceZone(1, "100",false, true);
        Assertions.assertEquals(1, rule.getZones().size());
        Assertions.assertEquals("100", rule.getZones().get(0));
    }
}