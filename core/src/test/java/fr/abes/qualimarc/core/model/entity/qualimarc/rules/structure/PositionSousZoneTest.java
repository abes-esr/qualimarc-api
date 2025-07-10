package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.positions.PositionsOperator;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
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
        PositionsOperator positionsOperator1 = new PositionsOperator(1, 1, ComparaisonOperateur.EGAL);
        PositionsOperator positionsOperator2 = new PositionsOperator(2, 2, ComparaisonOperateur.EGAL);
        PositionsOperator positionsOperatorMoins1 = new PositionsOperator(4, -1, ComparaisonOperateur.EGAL);
        PositionsOperator positionsOperator3 = new PositionsOperator(3, 3, ComparaisonOperateur.EGAL);

        PositionSousZone rule = new PositionSousZone(1, "606", "3", Lists.newArrayList(positionsOperator1), BooleanOperateur.OU);
        Assertions.assertTrue(rule.isValid(notice));

        PositionSousZone rule2 = new PositionSousZone(1, "801", "3", Lists.newArrayList(positionsOperator1), BooleanOperateur.OU);
        Assertions.assertFalse(rule2.isValid(notice));

        PositionSousZone rule3 = new PositionSousZone(1, "801", "a", Lists.newArrayList(positionsOperator2), BooleanOperateur.OU);
        Assertions.assertFalse(rule3.isValid(notice));

        PositionSousZone rule4 = new PositionSousZone(1, "713", "a", Lists.newArrayList(positionsOperator2), BooleanOperateur.OU);
        Assertions.assertFalse(rule4.isValid(notice));

        PositionSousZone rule5 = new PositionSousZone(1, "801", "2", Lists.newArrayList(positionsOperatorMoins1), BooleanOperateur.OU);
        Assertions.assertTrue(rule5.isValid(notice));

        PositionSousZone rule6 = new PositionSousZone(1, "801", "c", Lists.newArrayList(positionsOperatorMoins1), BooleanOperateur.OU);
        Assertions.assertFalse(rule6.isValid(notice));

        PositionSousZone rule7 = new PositionSousZone(1, "801", "c", Lists.newArrayList(positionsOperator1, positionsOperator3), BooleanOperateur.OU);
        Assertions.assertTrue(rule7.isValid(notice));

        PositionSousZone rule8 = new PositionSousZone(1, "801", "c", Lists.newArrayList(positionsOperator1, positionsOperator3), BooleanOperateur.ET);
        Assertions.assertFalse(rule8.isValid(notice));

        PositionSousZone rule9 = new PositionSousZone(1, "607", "x", Lists.newArrayList(positionsOperator3, positionsOperatorMoins1), BooleanOperateur.ET);
        Assertions.assertTrue(rule9.isValid(notice));

        PositionSousZone rule10 = new PositionSousZone(1, "607", "a", Lists.newArrayList(positionsOperator3, positionsOperatorMoins1), BooleanOperateur.ET);
        Assertions.assertFalse(rule10.isValid(notice));

        PositionsOperator positionsOperator1different = new PositionsOperator(1, 1, ComparaisonOperateur.DIFFERENT);
        PositionSousZone rule11 = new PositionSousZone(1, "607", "a", Lists.newArrayList(positionsOperator1different), BooleanOperateur.ET);
        Assertions.assertTrue(rule11.isValid(notice));

        PositionsOperator positionsOperator1inferieur = new PositionsOperator(1, 1, ComparaisonOperateur.INFERIEUR);
        PositionSousZone rule12 = new PositionSousZone(1, "607", "v", Lists.newArrayList(positionsOperator1inferieur), BooleanOperateur.ET);
        Assertions.assertFalse(rule12.isValid(notice));
    }

    @Test
    @DisplayName("test getZones")
    void getZones() {
        PositionsOperator positionsOperator2 = new PositionsOperator(2, 2, ComparaisonOperateur.EGAL);
        PositionSousZone rule = new PositionSousZone(1, "200", "a", Lists.newArrayList(positionsOperator2), BooleanOperateur.OU);
        Assertions.assertEquals(1, rule.getZones().size());
        Assertions.assertEquals("200$a", rule.getZones().get(0));
    }
}
