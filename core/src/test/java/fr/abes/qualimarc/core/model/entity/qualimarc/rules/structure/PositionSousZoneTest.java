package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
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
public class PositionSousZoneTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice1;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        PositionSousZone rule = new PositionSousZone(1, "606", "3", 1);
        Assertions.assertTrue(rule.isValid(notice));

        PositionSousZone rule2 = new PositionSousZone(1, "801", "3", 1);
        Assertions.assertFalse(rule2.isValid(notice));

        PositionSousZone rule3 = new PositionSousZone(1, "801", "a", 2);
        Assertions.assertFalse(rule3.isValid(notice));

        PositionSousZone rule4 = new PositionSousZone(1, "713", "a", 2);
        Assertions.assertFalse(rule4.isValid(notice));
    }

    @Test
    @DisplayName("test getZones")
    void getZones() {
        PositionSousZone rule = new PositionSousZone(1, "200", "a", 2);
        Assertions.assertEquals("200$a", rule.getZones());
    }
}
