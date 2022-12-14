package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.utils.TypeVerification;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {TypeDocument.class})
class TypeDocumentTest {
    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    private NoticeXml notice;

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice2;

    private NoticeXml noticeWrongSize008;

    @BeforeEach
    void init() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        this.notice = mapper.readValue(xml, NoticeXml.class);
    }

    @Test
    @DisplayName("test typeDocument : cas général")
    void isValid() {
        TypeDocument typeDocument = new TypeDocument(1, TypeVerification.STRICTEMENT, 2, "a");
        Assertions.assertTrue(typeDocument.isValid(this.notice));

        TypeDocument typeDocument1 = new TypeDocument(2, TypeVerification.STRICTEMENT, 2, "b");
        Assertions.assertFalse(typeDocument1.isValid(notice));

        TypeDocument typeDocument2 = new TypeDocument(3, TypeVerification.STRICTEMENTDIFFERENT, 1, "b");
        Assertions.assertTrue(typeDocument2.isValid(notice));

        TypeDocument typeDocument3 = new TypeDocument(4, TypeVerification.STRICTEMENTDIFFERENT, 3, "x");
        Assertions.assertFalse(typeDocument3.isValid(notice));
    }

    @Test
    @DisplayName("test typeDocument : cas d'un operateur non géré par cette règle")
    void isValidWithWrongOperateur() {
        TypeDocument typeDocument = new TypeDocument(1, TypeVerification.COMMENCE, 1, "a");
        Assertions.assertThrows(IllegalArgumentException.class, () -> typeDocument.isValid(notice));

        typeDocument.setTypeDeVerification(TypeVerification.CONTIENT);
        Assertions.assertThrows(IllegalArgumentException.class, () -> typeDocument.isValid(notice));

        typeDocument.setTypeDeVerification(TypeVerification.TERMINE);
        Assertions.assertThrows(IllegalArgumentException.class, () -> typeDocument.isValid(notice));

        typeDocument.setTypeDeVerification(TypeVerification.NECONTIENTPAS);
        Assertions.assertThrows(IllegalArgumentException.class, () -> typeDocument.isValid(notice));
    }

    @Test
    @DisplayName("test typeDocument : cas d'une valeur de 008 de moins de 4 caractères")
    void isValidWithWrongSize() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice2.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);

        TypeDocument typeDocument = new TypeDocument(1, TypeVerification.STRICTEMENT, 4, "a");
        Assertions.assertTrue(typeDocument.isValid(notice));
    }

    @Test
    void getZone() {
        TypeDocument typeDocument = new TypeDocument(1, TypeVerification.STRICTEMENTDIFFERENT, 1, "a");
        Assertions.assertEquals(1, typeDocument.getZones().size());
        Assertions.assertEquals("008", typeDocument.getZones().get(0));
    }

}