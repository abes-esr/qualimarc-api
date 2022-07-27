package fr.abes.qualimarc.core.model.entity.rules.structure;

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

@SpringBootTest(classes = {PresenceSousZone.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class PresenceSousZoneTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice1;

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        PresenceSousZone rule0 = new PresenceSousZone(1, "la zone n'existe pas", "190", "a", Priority.P1, true);
        Assertions.assertFalse(rule0.isValid(notice));

        PresenceSousZone rule1 = new PresenceSousZone(1, "la sous-zone $j doit être présente et elle est présente", "010", "a", Priority.P1,  true);
        Assertions.assertTrue(rule1.isValid(notice));

        PresenceSousZone rule2 = new PresenceSousZone(2, "la sous-zone $a doit être présente mais elle n'est pas présente", "010", "j", Priority.P1, true);
        Assertions.assertFalse(rule2.isValid(notice));

        PresenceSousZone rule3 = new PresenceSousZone(3, "la sous-zone $b ne doit pas être présente mais elle est présente", "020", "a", Priority.P1, false);
        Assertions.assertFalse(rule3.isValid(notice));

        PresenceSousZone rule4 = new PresenceSousZone(4, "la sous-zone $c ne doit pas être présente et elle n'est pas présente", "020", "j", Priority.P1, false);
        Assertions.assertTrue(rule4.isValid(notice));
    }
}