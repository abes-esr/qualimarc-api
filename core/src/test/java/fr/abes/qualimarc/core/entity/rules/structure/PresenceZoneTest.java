package fr.abes.qualimarc.core.entity.rules.structure;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {PresenceZone.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class PresenceZoneTest {
    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        NoticeXml notice;
        XmlMapper mapper = new XmlMapper();
        notice = mapper.readValue(xml.getBytes(StandardCharsets.UTF_8), NoticeXml.class);

        PresenceZone rule = new PresenceZone(1, "La 010 zone doit être présente", "010", true);
        Assertions.assertEquals(true, rule.isValid(notice));
    }
}