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

    @Value("classpath:555555555.xml")
    private Resource xmlFileNotice2;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        PresenceSousZone rule1 = new PresenceSousZone(1, "puisque la ressource n'est pas de type audiovisuel ni multimédia, la sous-zone $j n'a pas lieu d'être", "101", "j", true);
        Assertions.assertTrue(rule1.isValid(notice));

        PresenceSousZone rule2 = new PresenceSousZone(1, "puisque la ressource n'est pas de type audiovisuel ni multimédia, la sous-zone $j n'a pas lieu d'être", "101", "j", false);
        Assertions.assertFalse(rule2.isValid(notice));

        String xml2 = IOUtils.toString(new FileInputStream(xmlFileNotice2.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module2 = new JacksonXmlModule();
        module2.setDefaultUseWrapper(false);
        XmlMapper mapper2 = new XmlMapper(module2);
        NoticeXml notice2 = mapper2.readValue(xml2, NoticeXml.class);

        PresenceSousZone rule3 = new PresenceSousZone(1, "puisque la ressource n'est pas de type audiovisuel ni multimédia, la sous-zone $j n'a pas lieu d'être", "101", "j", true);
        Assertions.assertFalse(rule3.isValid(notice2));

        PresenceSousZone rule4 = new PresenceSousZone(1, "puisque la ressource n'est pas de type audiovisuel ni multimédia, la sous-zone $j n'a pas lieu d'être", "101", "j", false);
        Assertions.assertTrue(rule4.isValid(notice2));
    }
}
