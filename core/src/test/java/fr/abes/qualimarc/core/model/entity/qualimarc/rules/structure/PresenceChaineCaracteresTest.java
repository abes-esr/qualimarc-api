package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.EnumChaineCaracteres;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {PresenceChaineCaracteres.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(BaseXMLConfiguration.class))
class PresenceChaineCaracteresTest {

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    private NoticeXml notice;

    @BeforeEach
    void init() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        this.notice = mapper.readValue(xml, NoticeXml.class);
    }

    @Test
    @DisplayName("Zone absente")
    void isValid() throws IOException {
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(0, "999", "", EnumChaineCaracteres.STRICTEMENT, "");
        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));
    }


    @Test
    @DisplayName("Sous-zone absente")
    void isValid0() throws IOException {
        PresenceChaineCaracteres presenceChaineCaracteres0 = new PresenceChaineCaracteres(0, "200", "g", EnumChaineCaracteres.STRICTEMENT, "");
        Assertions.assertFalse(presenceChaineCaracteres0.isValid(notice));
    }


    @Test
    @DisplayName("STRICTEMENT les chaine de caractères")
    void isValid1() throws IOException {
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.STRICTEMENT, "Texte imprimé");
        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));
    }

    @Test
    @DisplayName("STRICTEMENT les chaines de caractères")
    void isValid2() throws IOException {
        //  Teste si une occurence de la sous-zone contient STRICTEMENT la chaine de caractères
        ChaineCaracteres chaineCaracteres2a = new ChaineCaracteres(BooleanOperateur.OU, EnumChaineCaracteres.STRICTEMENT, "Texte");
        ChaineCaracteres chaineCaracteres2b = new ChaineCaracteres(BooleanOperateur.OU, EnumChaineCaracteres.STRICTEMENT, "Texte imprimé");
        List<ChaineCaracteres> listChainesCaracteres2 = new ArrayList<>();
        listChainesCaracteres2.add(chaineCaracteres2a);
        listChainesCaracteres2.add(chaineCaracteres2b);
        PresenceChaineCaracteres presenceChaineCaracteres2 = new PresenceChaineCaracteres(1, "200", "b", EnumChaineCaracteres.STRICTEMENT, "", listChainesCaracteres2);

        Assertions.assertTrue(presenceChaineCaracteres2.isValid(notice));
    }

//        // Teste si une occurence de la sous-zone CONTIENT la/les chaines de caractères
//        ChaineCaracteres chaineCaracteres3a = new ChaineCaracteres(BooleanOperateur.ET, EnumChaineCaracteres.CONTIENT, "Convention");
//        ChaineCaracteres chaineCaracteres3b = new ChaineCaracteres(BooleanOperateur.OU, EnumChaineCaracteres.CONTIENT, "brochure");
//        List<ChaineCaracteres> listChainesCaracteres3 = new ArrayList<>();
//        listChainesCaracteres3.add(chaineCaracteres3a);
//        listChainesCaracteres3.add(chaineCaracteres3b);
//        PresenceChaineCaracteres presenceChaineCaracteres3 = new PresenceChaineCaracteres(1, "200", "a", listChainesCaracteres3);
//
//        Assertions.assertTrue(presenceChaineCaracteres3.isValid(notice));
//
//
//        // Teste si une occurence de la sous-zone ne CONTIENT pas la/les chaines de caractères
//        ChaineCaracteres chaineCaracteres4a = new ChaineCaracteres(BooleanOperateur.ET, EnumChaineCaracteres.CONTIENT, "Texte");
//        ChaineCaracteres chaineCaracteres4b = new ChaineCaracteres(BooleanOperateur.OU, EnumChaineCaracteres.CONTIENT, "brochure");
//        List<ChaineCaracteres> listChainesCaracteres4 = new ArrayList<>();
//        listChainesCaracteres4.add(chaineCaracteres4a);
//        listChainesCaracteres4.add(chaineCaracteres4b);
//        PresenceChaineCaracteres presenceChaineCaracteres4 = new PresenceChaineCaracteres(1, "200", "a", listChainesCaracteres4);
//
//        Assertions.assertFalse(presenceChaineCaracteres4.isValid(notice));
//
//
//        // Test si une occurence de la sous-zone COMMENCE par la/les chaines de caractères
//        ChaineCaracteres chaineCaracteres5a = new ChaineCaracteres(BooleanOperateur.OU, EnumChaineCaracteres.COMMENCE, "Texte");
//        ChaineCaracteres chaineCaracteres5b = new ChaineCaracteres(BooleanOperateur.OU, EnumChaineCaracteres.COMMENCE, "Convention");
//        List<ChaineCaracteres> listChainesCaracteres5 = new ArrayList<>();
//        listChainesCaracteres5.add(chaineCaracteres5a);
//        listChainesCaracteres5.add(chaineCaracteres5b);
//        PresenceChaineCaracteres presenceChaineCaracteres5 = new PresenceChaineCaracteres(1, "200", "a", listChainesCaracteres5);
//
//        Assertions.assertTrue(presenceChaineCaracteres5.isValid(notice));


    @Test
    @DisplayName("test getZones")
    void getZones() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres();
        List<ChaineCaracteres> listChaineCaracteres = new ArrayList<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres rule = new PresenceChaineCaracteres(1, "020", "a", EnumChaineCaracteres.STRICTEMENT, "", listChaineCaracteres);

        Assertions.assertEquals("020$a", rule.getZones());
    }
}