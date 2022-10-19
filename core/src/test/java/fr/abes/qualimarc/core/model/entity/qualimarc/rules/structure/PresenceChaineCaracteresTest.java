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

    @Test
    void isValid() throws IOException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper mapper = new XmlMapper(module);
        NoticeXml notice = mapper.readValue(xml, NoticeXml.class);


        // Teste la présence de la zone
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres(BooleanOperateur.ET, EnumChaineCaracteres.UNIQUEMENT, "");
        List<ChaineCaracteres> listChaineCaracteres = new ArrayList<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres presenceChaineCaracteres = new PresenceChaineCaracteres(0, "999", "", listChaineCaracteres);

        Assertions.assertFalse(presenceChaineCaracteres.isValid(notice));


        // Teste la présence de la sous-zone
        ChaineCaracteres chaineCaracteres0 = new ChaineCaracteres(BooleanOperateur.ET, EnumChaineCaracteres.UNIQUEMENT, "");
        List<ChaineCaracteres> listChaineCaracteres0 = new ArrayList<>();
        listChaineCaracteres0.add(chaineCaracteres0);
        PresenceChaineCaracteres presenceChaineCaracteres0 = new PresenceChaineCaracteres(0, "200", "g", listChaineCaracteres0);

        Assertions.assertFalse(presenceChaineCaracteres0.isValid(notice));


        //  Teste si une occurence de la sous-zone contient STRICTEMENT la chaine de caractères
        ChaineCaracteres chaineCaracteres1 = new ChaineCaracteres(BooleanOperateur.ET, EnumChaineCaracteres.UNIQUEMENT, "Texte imprimé");
        List<ChaineCaracteres> listChainesCaracteres1 = new ArrayList<>();
        listChainesCaracteres1.add(chaineCaracteres1);
        PresenceChaineCaracteres presenceChaineCaracteres1 = new PresenceChaineCaracteres(1, "200", "b", listChainesCaracteres1);

        Assertions.assertTrue(presenceChaineCaracteres1.isValid(notice));


        //  Teste si une occurence de la sous-zone contient STRICTEMENT la chaine de caractères
        ChaineCaracteres chaineCaracteres2 = new ChaineCaracteres(BooleanOperateur.ET, EnumChaineCaracteres.UNIQUEMENT, "Texte imprime");
        List<ChaineCaracteres> listChainesCaracteres2 = new ArrayList<>();
        listChainesCaracteres2.add(chaineCaracteres2);
        PresenceChaineCaracteres presenceChaineCaracteres2 = new PresenceChaineCaracteres(1, "200", "b", listChainesCaracteres2);

        Assertions.assertFalse(presenceChaineCaracteres2.isValid(notice));

        // TODO trouver pourquoi ce test ne passe pas puis continuer les tests
        // Teste si une occurence de la sous-zone CONTIENT la/les chaines de caractères
        ChaineCaracteres chaineCaracteres3a = new ChaineCaracteres(BooleanOperateur.ET, EnumChaineCaracteres.CONTIENT, "collective");
//        ChaineCaracteres chaineCaracteres3b = new ChaineCaracteres(BooleanOperateur.OU, EnumChaineCaracteres.CONTIENT, "tartufe");
        List<ChaineCaracteres> listChainesCaracteres3 = new ArrayList<>();
        listChainesCaracteres1.add(chaineCaracteres3a);
//        listChainesCaracteres1.add(chaineCaracteres3b);
        PresenceChaineCaracteres presenceChaineCaracteres3 = new PresenceChaineCaracteres(1, "200", "a", listChainesCaracteres3);

        Assertions.assertTrue(presenceChaineCaracteres3.isValid(notice));

    }

    @Test
    @DisplayName("test getZones")
    void getZones() {
        ChaineCaracteres chaineCaracteres = new ChaineCaracteres();
        List<ChaineCaracteres> listChaineCaracteres = new ArrayList<>();
        listChaineCaracteres.add(chaineCaracteres);
        PresenceChaineCaracteres rule = new PresenceChaineCaracteres(1, "020", "a", listChaineCaracteres);

        Assertions.assertEquals("020$a", rule.getZones());
    }
}