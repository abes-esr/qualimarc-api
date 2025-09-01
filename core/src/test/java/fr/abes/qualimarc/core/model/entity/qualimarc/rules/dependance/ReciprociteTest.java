package fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {Reciprocite.class})
public class ReciprociteTest {
    @Value("classpath:143519379.xml")
    Resource xmlFileNotice;

    @Value("classpath:02787088X.xml")
    Resource xmlFileNoticeLiee;

    NoticeXml notice;
    NoticeXml noticeLiee;

    @BeforeEach
    void init() throws IOException {
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);

        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        notice = mapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlFileNoticeLiee.getFile()), StandardCharsets.UTF_8);
        noticeLiee = mapper.readValue(xml, NoticeXml.class);
    }

    @Test
    void isValid() {
        //cas ou le PPN est dans la notice liée : une seule zone / sous zone
        Reciprocite rule = new Reciprocite(1, "455",false, "0");
        Assertions.assertFalse(rule.isValid(notice, noticeLiee));

        //cas ou le PPN n'est pas dans la notice liée : une seule zone / sous zone
        rule = new Reciprocite(1, "456",false, "0");
        Assertions.assertTrue(rule.isValid(notice, noticeLiee));

        //cas ou le PPN est dans la notice liée : plusieurs zones
        rule = new Reciprocite(1, "457",false, "0");
        Assertions.assertFalse(rule.isValid(notice, noticeLiee));

        //cas ou le PPN n'est pas dans la notice liée : plusieurs zones
        rule = new Reciprocite(1, "458", false,"0");
        Assertions.assertTrue(rule.isValid(notice, noticeLiee));

        //cas ou le PPN est dans la notice liée : plusieurs sous zones
        rule = new Reciprocite(1, "459",false, "0");
        Assertions.assertFalse(rule.isValid(notice, noticeLiee));

        //cas ou le PPN n'est pas dans la notice liée : plusieurs sous zones
        rule = new Reciprocite(1, "460",false, "0");
        Assertions.assertTrue(rule.isValid(notice, noticeLiee));
    }

    @Test
    void getZones() {
        Reciprocite rule = new Reciprocite(1, "200",false, "a");
        Assertions.assertEquals(1, rule.getZones().size());
        Assertions.assertEquals("200$a", rule.getZones().get(0));
    }
}