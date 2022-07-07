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

@SpringBootTest(classes = {PresenceSousZone.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class PresenceSousZoneTest {

    @Value("classpath:444444444.xml")
    private Resource xmlFileNotice1;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        PresenceSousZone rule1 = new PresenceSousZone(1, "la sous-zone $j doit être présente et elle est présente", "101", "j", true);
        Assertions.assertTrue(rule1.isValid(notice));

        PresenceSousZone rule2 = new PresenceSousZone(2, "la sous-zone $a doit être présente mais elle n'est pas présente", "101", "a", true);
        Assertions.assertFalse(rule2.isValid(notice));

        PresenceSousZone rule3 = new PresenceSousZone(3, "la sous-zone $b ne doit pas être présente mais elle est présente", "101", "b", false);
        Assertions.assertFalse(rule3.isValid(notice));

        PresenceSousZone rule4 = new PresenceSousZone(4, "la sous-zone $c ne doit pas être présente et elle n'est pas présente", "101", "c", false);
        Assertions.assertTrue(rule4.isValid(notice));
    }
}