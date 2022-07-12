package fr.abes.qualimarc.core.model.entity.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
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

        PresenceZone rule = new PresenceZone(1, "La zone 010 doit être présente", "010", true);
        Assertions.assertEquals(true, rule.isValid(notice));

        PresenceZone rule2 = new PresenceZone(2, "La zone 011 doit être absente", "011", false);
        Assertions.assertEquals(true, rule2.isValid(notice));

        PresenceZone rule3 = new PresenceZone(3, "La zone 011 doit être présente", "011", true);
        Assertions.assertEquals(false, rule3.isValid(notice));

        PresenceZone rule4 = new PresenceZone(4, "La zone 010 doit être absente", "010", false);
        Assertions.assertEquals(false, rule4.isValid(notice));
    }
}