package fr.abes.qualimarc.web.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.NombreSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.NombreZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {UtilsMapper.class, ObjectMapper.class, WebDtoMapper.class})
public class WebDtoMapperTest {
    @Autowired
    UtilsMapper mapper;

    @Test
    @DisplayName("Test Mapper resultAnalyseResponseDto")
    void testResultAnalyseResponseDtoMapper() {
        ResultAnalyse resultAnalyse = new ResultAnalyse();

        resultAnalyse.addPpnAnalyse("111111111");
        resultAnalyse.addPpnAnalyse("222222222");
        resultAnalyse.addPpnAnalyse("333333333");
        resultAnalyse.addPpnAnalyse("444444444");
        resultAnalyse.addPpnAnalyse("555555555");
        resultAnalyse.addPpnAnalyse("666666666");
        resultAnalyse.addPpnAnalyse("777777777");
        resultAnalyse.addPpnAnalyse("888888888");

        resultAnalyse.addPpnErrone("111111111");
        resultAnalyse.addPpnOk("222222222");
        resultAnalyse.addPpnOk("555555555");
        resultAnalyse.addPpnErrone("888888888");
        resultAnalyse.addPpnErrone("777777777");
        resultAnalyse.addPpnInconnu("666666666");
        resultAnalyse.addPpnInconnu("444444444");
        resultAnalyse.addPpnInconnu("333333333");

        ResultRules result1 = new ResultRules("111111111");
        result1.addMessage("Message 1");
        result1.addMessage("Message 2");

        ResultRules result2 = new ResultRules("888888888");
        result2.addMessage("Message 3");
        result2.addMessage("Message 4");
        result2.addMessage("Message 5");

        ResultRules result3 = new ResultRules("777777777");
        result3.addMessage("Message 6");

        resultAnalyse.addResultRule(result1);
        resultAnalyse.addResultRule(result2);
        resultAnalyse.addResultRule(result3);

        ResultAnalyseResponseDto responseDto = mapper.map(resultAnalyse, ResultAnalyseResponseDto.class);

        Assertions.assertEquals(3, responseDto.getResultRules().size());
        Assertions.assertEquals(2, responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("111111111")).findFirst().get().getMessages().size());
        Assertions.assertEquals(3, responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("888888888")).findFirst().get().getMessages().size());
        Assertions.assertEquals(1, responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("777777777")).findFirst().get().getMessages().size());
        Assertions.assertEquals(3, responseDto.getNbPpnErrones());
        Assertions.assertEquals(8, responseDto.getNbPpnAnalyses());
        Assertions.assertEquals(3, responseDto.getNbPpnInconnus());
        Assertions.assertEquals(2, responseDto.getNbPpnOk());
        Assertions.assertEquals(8, responseDto.getPpnAnalyses().size());
        Assertions.assertEquals(3, responseDto.getPpnErrones().size());
        Assertions.assertEquals(3, responseDto.getPpnInconnus().size());
        Assertions.assertEquals(2, responseDto.getPpnOk().size());
    }

    /**
     * Test du mapper converterPresenceZone
     */
    @Test
    @DisplayName("Test Mapper converterPresenceZone")
    void converterPresenceZoneTest() {
        //  Préparation d'un objet PresenceZoneWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        PresenceZoneWebDto presenceZoneWebDto = new PresenceZoneWebDto(1, 1, "message 1", "100", Priority.P1, typeDoc, true);

        //  Appel du mapper
        PresenceZone responseDto = mapper.map(presenceZoneWebDto, PresenceZone.class);

        //  Contrôle de la bonne conformité des résultats
        Assertions.assertEquals(presenceZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(presenceZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(presenceZoneWebDto.getZone(), responseDto.getZone());
        Assertions.assertEquals(presenceZoneWebDto.getPriority(), responseDto.getPriority());
        Assertions.assertEquals(presenceZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().collect(Collectors.toList()).get(0).getId());
        Assertions.assertEquals(presenceZoneWebDto.isPresent(), responseDto.isPresent());
    }

    /**
     * Test du mapper converterPresenceSousZone
     */
    @Test
    @DisplayName("Test Mapper converterPresenceSousZone")
    void converterPresenceSousZoneTest() {
        //  Préparation d'un objet PresenceSousZoneWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        PresenceSousZoneWebDto presenceSousZoneWebDto = new PresenceSousZoneWebDto(1, 1, "message 1", "100", Priority.P1, typeDoc, "a", true);

        //  Appel du mapper
        PresenceSousZone responseDto = mapper.map(presenceSousZoneWebDto, PresenceSousZone.class);

        //  Contrôle de la bonne conformité des résultats
        Assertions.assertEquals(presenceSousZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(presenceSousZoneWebDto.getZone(), responseDto.getZone());
        Assertions.assertEquals(presenceSousZoneWebDto.getPriority(), responseDto.getPriority());
        Assertions.assertEquals(presenceSousZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().collect(Collectors.toList()).get(0).getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getSousZone(), responseDto.getSousZone());
        Assertions.assertEquals(presenceSousZoneWebDto.isPresent(), responseDto.isPresent());
    }

    /**
     * Test du mapper converterNombreZone
     */
    @Test
    @DisplayName("Test Mapper converterNombreZone")
    void converterNombreZoneTest() {
        //  Préparation d'un objet NombreZoneWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        NombreZoneWebDto nombreZoneWebDto = new NombreZoneWebDto(1, 1, "message 1", "100", Priority.P1, typeDoc, Operateur.EGAL, 1);

        //  Appel du mapper
        NombreZone responseDto = mapper.map(nombreZoneWebDto, NombreZone.class);

        //  Contrôle de la bonne conformité des résultats
        Assertions.assertEquals(nombreZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(nombreZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(nombreZoneWebDto.getZone(), responseDto.getZone());
        Assertions.assertEquals(nombreZoneWebDto.getPriority(), responseDto.getPriority());
        Assertions.assertEquals(nombreZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().collect(Collectors.toList()).get(0).getId());
        Assertions.assertEquals(nombreZoneWebDto.getOperateur(), responseDto.getOperateur());
        Assertions.assertEquals(nombreZoneWebDto.getOccurrences(), responseDto.getOccurrences());
    }

    /**
     * Test du mapper converterNombreSousZoneTest
     */
    @Test
    @DisplayName("Test Mapper converterNombreSousZoneTest")
    void converterNombreSousZoneTest() {
        //  Préparation d'un objet NombreSousZoneWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        NombreSousZoneWebDto nombreSousZoneWebDto = new NombreSousZoneWebDto(1, 1, "message 1", "100", Priority.P1, typeDoc, "a", "100", "a");

        //  Appel du mapper
        NombreSousZone responseDto = mapper.map(nombreSousZoneWebDto, NombreSousZone.class);

        //  Contrôle de la bonne conformité des résultats
        Assertions.assertEquals(1, responseDto.getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(nombreSousZoneWebDto.getZone(), responseDto.getZone());
        Assertions.assertEquals(nombreSousZoneWebDto.getPriority(), responseDto.getPriority());
        Assertions.assertEquals(nombreSousZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().collect(Collectors.toList()).get(0).getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getSousZone(), responseDto.getSousZone());
        Assertions.assertEquals(nombreSousZoneWebDto.getZoneCible(), responseDto.getZoneCible());
        Assertions.assertEquals(nombreSousZoneWebDto.getSousZoneCible(), responseDto.getSousZoneCible());
    }

    /**
     * Test du mapper converterEditeurCreeWebDtoTest
     */
    @Test
    @DisplayName("Test Mapper converterEditeurCreeWebDtoTest")
    void converterEditeurCreeWebDtoTest() {
        //  Préparation d'un objet ResultAnalyse
        ResultAnalyse resultAnalyse = new ResultAnalyse();
        ResultRules resultRules = new ResultRules("123456789");
        resultAnalyse.addResultRule(resultRules);
        resultAnalyse.addPpnAnalyse("4");
        resultAnalyse.addPpnErrone("1");
        resultAnalyse.addPpnOk("3");
        resultAnalyse.addPpnInconnu("0");

        //  Appel du mapper
        ResultAnalyseResponseDto responseDto = mapper.map(resultAnalyse, ResultAnalyseResponseDto.class);

        //  Contrôle de la bonne conformité des résultats
        Assertions.assertEquals(resultAnalyse.getResultRules().get(0).getPpn(), responseDto.getResultRules().get(0).getPpn());
        Assertions.assertEquals(resultAnalyse.getPpnAnalyses().size(), responseDto.getNbPpnAnalyses());
        Assertions.assertEquals(resultAnalyse.getPpnErrones().size(), responseDto.getNbPpnErrones());
        Assertions.assertEquals(resultAnalyse.getPpnOk().size(), responseDto.getNbPpnOk());
        Assertions.assertEquals(resultAnalyse.getPpnInconnus().size(), responseDto.getNbPpnInconnus());
    }
}
