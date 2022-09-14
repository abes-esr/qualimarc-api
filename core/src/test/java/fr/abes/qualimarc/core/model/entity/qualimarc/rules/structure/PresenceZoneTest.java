package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.Priority;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {PresenceZone.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class PresenceZoneTest {
    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        PresenceZone rule = new PresenceZone(1, "La zone 010 doit être présente", "010", Priority.P1, true);
        Assertions.assertTrue(rule.isValid(notice));

        PresenceZone rule2 = new PresenceZone(2, "La zone 011 doit être absente", "011", Priority.P1, false);
        Assertions.assertTrue(rule2.isValid(notice));

        PresenceZone rule3 = new PresenceZone(3, "La zone 011 doit être présente", "011", Priority.P1, true);
        Assertions.assertFalse(rule3.isValid(notice));

        PresenceZone rule4 = new PresenceZone(4, "La zone 010 doit être absente", "010", Priority.P1, false);
        Assertions.assertFalse(rule4.isValid(notice));
    }
}