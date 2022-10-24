package fr.abes.qualimarc.web.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.Indicateur;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.NombreCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.*;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRule;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.IndicateurWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.NombreCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        result1.setFamilleDocument(new FamilleDocument("A", "Monographie"));
        result1.addMessage("Message 1");
        result1.addMessage("Message 2");

        ResultRules result2 = new ResultRules("888888888");
        result2.setFamilleDocument(new FamilleDocument("BD", "Ressource Continue"));
        result2.addMessage("Message 3");
        result2.addMessage("Message 4");
        result2.addMessage("Message 5");

        ResultRules result3 = new ResultRules("777777777");
        result3.setFamilleDocument(new FamilleDocument("O", "Doc Elec"));
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
        PresenceZoneWebDto presenceZoneWebDto = new PresenceZoneWebDto(1, 1, "message 1", "100", "P1", typeDoc, true);

        //  Appel du mapper
        ComplexRule responseDto = mapper.map(presenceZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PresenceZone presenceZone = (PresenceZone) responseDto.getFirstRule();
        Assertions.assertEquals(presenceZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(presenceZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(presenceZoneWebDto.getPriority(), responseDto.getPriority().toString());
        Assertions.assertEquals(presenceZoneWebDto.getTypesDoc().size(), responseDto.getFamillesDocuments().size());
        Assertions.assertEquals(presenceZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(presenceZoneWebDto.getId(), presenceZone.getId());
        Assertions.assertEquals(presenceZoneWebDto.getZone(), presenceZone.getZone());
        Assertions.assertEquals(presenceZoneWebDto.isPresent(), presenceZone.isPresent());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceZoneWebDto(1, 1, "message 1", "100", null, typeDoc, true), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceZoneWebDto(1, 1, null, "100", "P1", typeDoc, true), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

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
        PresenceSousZoneWebDto presenceSousZoneWebDto = new PresenceSousZoneWebDto(1, 1, "message 1", "100", "P1", typeDoc, "a", true);

        //  Appel du mapper
        ComplexRule responseDto = mapper.map(presenceSousZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PresenceSousZone presenceZone = (PresenceSousZone) responseDto.getFirstRule();
        Assertions.assertEquals(presenceSousZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(presenceSousZoneWebDto.getPriority(), responseDto.getPriority().toString());
        Assertions.assertEquals(presenceSousZoneWebDto.getTypesDoc().size(), responseDto.getFamillesDocuments().size());
        Assertions.assertEquals(presenceSousZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getSousZone(), presenceZone.getSousZone());
        Assertions.assertEquals(presenceSousZoneWebDto.getId(), presenceZone.getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getZone(), presenceZone.getZone());
        Assertions.assertEquals(presenceSousZoneWebDto.isPresent(), presenceZone.isPresent());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceSousZoneWebDto(1, 1, "message 1", "100", null, typeDoc, "a", true), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceSousZoneWebDto(1, 1, null, "100", "P1", typeDoc, "a", true), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

    }

    /**
     * Test du mapper converterNombreZone
     */
    @Test
    @DisplayName("Test Mapper converterNombreZone")
    void converterNombreZoneTest() {
        NombreZoneWebDto nombreZoneWebDto0 = new NombreZoneWebDto(1, 1, "test", "100", "P1", null, Operateur.INFERIEUR_EGAL, 1);
        Exception ex = Assertions.assertThrows(MappingException.class, () -> mapper.map(nombreZoneWebDto0, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle", ex.getCause().getMessage());

        NombreZoneWebDto nombreZoneWebDto1 = new NombreZoneWebDto(1, 1, "test", "100", "P1", null, Operateur.SUPERIEUR_EGAL, 1);
        ex = Assertions.assertThrows(MappingException.class, () -> mapper.map(nombreZoneWebDto1, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle", ex.getCause().getMessage());

        //  Préparation d'un objet NombreZoneWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        NombreZoneWebDto nombreZoneWebDto = new NombreZoneWebDto(1, 1, "message 1", "100", "P1", typeDoc, Operateur.EGAL, 1);

        //  Appel du mapper
        ComplexRule responseDto = mapper.map(nombreZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        NombreZone presenceZone = (NombreZone) responseDto.getFirstRule();
        Assertions.assertEquals(nombreZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(nombreZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(nombreZoneWebDto.getPriority(), responseDto.getPriority().toString());
        Assertions.assertEquals(nombreZoneWebDto.getTypesDoc().size(), responseDto.getFamillesDocuments().size());
        Assertions.assertEquals(nombreZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(nombreZoneWebDto.getOperateur(), presenceZone.getOperateur());
        Assertions.assertEquals(nombreZoneWebDto.getOccurrences(), presenceZone.getOccurrences());
        Assertions.assertEquals(nombreZoneWebDto.getId(), presenceZone.getId());
        Assertions.assertEquals(nombreZoneWebDto.getZone(), presenceZone.getZone());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreZoneWebDto(1, 1, "message 1", "100", null, typeDoc, Operateur.SUPERIEUR, 1), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreZoneWebDto(1, 1, null, "100", "P1", typeDoc, Operateur.SUPERIEUR, 1), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

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
        NombreSousZoneWebDto nombreSousZoneWebDto = new NombreSousZoneWebDto(1, 1, "message 1", "100", "P1", typeDoc, "a", "100", "a");

        //  Appel du mapper
        ComplexRule responseDto = mapper.map(nombreSousZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        NombreSousZone presenceZone = (NombreSousZone) responseDto.getFirstRule();
        Assertions.assertEquals(nombreSousZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(nombreSousZoneWebDto.getPriority(), responseDto.getPriority().toString());
        Assertions.assertEquals(nombreSousZoneWebDto.getTypesDoc().size(), responseDto.getFamillesDocuments().size());
        Assertions.assertEquals(nombreSousZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getSousZone(), presenceZone.getSousZone());
        Assertions.assertEquals(nombreSousZoneWebDto.getZoneCible(), presenceZone.getZoneCible());
        Assertions.assertEquals(nombreSousZoneWebDto.getSousZoneCible(), presenceZone.getSousZoneCible());
        Assertions.assertEquals(nombreSousZoneWebDto.getId(), presenceZone.getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getZone(), presenceZone.getZone());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreSousZoneWebDto(1, 1, "message 1", "100", null, typeDoc, "a", "200", "b"), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreSousZoneWebDto(1, 1, null, "100", "P1", typeDoc, "a", "200", "b"), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

    }

    /**
     * Test du mapper converterPositionSousZoneTest
     */
    @Test
    @DisplayName("Test Mapper converterPositionSousZoneTest")
    void converterPositionSousZoneTest() {
        //  Préparation d'un objet PositionSousZoneWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        PositionSousZoneWebDto positionSousZoneWebDto = new PositionSousZoneWebDto(1, 1, "message 1", "100", "P1", typeDoc, "a", 1);

        //  Appel du mapper
        ComplexRule responseDto = mapper.map(positionSousZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PositionSousZone presenceZone = (PositionSousZone) responseDto.getFirstRule();
        Assertions.assertEquals(positionSousZoneWebDto.getId(), responseDto.getId());
        Assertions.assertEquals(positionSousZoneWebDto.getMessage(), responseDto.getMessage());
        Assertions.assertEquals(positionSousZoneWebDto.getPriority(), responseDto.getPriority().toString());
        Assertions.assertEquals(positionSousZoneWebDto.getTypesDoc().size(), responseDto.getFamillesDocuments().size());
        Assertions.assertEquals(positionSousZoneWebDto.getTypesDoc().get(0), responseDto.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(positionSousZoneWebDto.getSousZone(), presenceZone.getSousZone());
        Assertions.assertEquals(positionSousZoneWebDto.getPosition(), presenceZone.getPosition());
        Assertions.assertEquals(positionSousZoneWebDto.getId(), presenceZone.getId());
        Assertions.assertEquals(positionSousZoneWebDto.getZone(), presenceZone.getZone());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PositionSousZoneWebDto(1, 1, "message 1", "100", null, typeDoc, "a", 2), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PositionSousZoneWebDto(1, 1, null, "100", "P1", typeDoc, "a", 2), ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

    }

    @Test
    @DisplayName("Test mapper converterPresenceSousZonesMemeZone")
    void converterPresenceSousZonesMemeZoneTest() {
        PresenceSousZonesMemeZoneWebDto rule1 = new PresenceSousZonesMemeZoneWebDto(1, "200", "ET");
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule1, ComplexRule.class));
        Assertions.assertEquals("L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule2 = new PresenceSousZonesMemeZoneWebDto(1, 1, null, "200", null, null);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule3 = new PresenceSousZonesMemeZoneWebDto(1, 1, "test", "200", "P1", new ArrayList<>());
        rule3.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true, BooleanOperateur.ET));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("La règle 1 doit avoir au moins deux sous-zones déclarées", exception.getCause().getMessage());

        rule3.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false, BooleanOperateur.ET));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : La première sous-zone ne doit pas avoir d'opérateur booléen", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule4 = new PresenceSousZonesMemeZoneWebDto(1, 1, "test", "200", "P1", new ArrayList<>());
        rule4.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true));
        rule4.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule4, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Les sous-zones en dehors de la première doivent avoir un opérateur booléen", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule5 = new PresenceSousZonesMemeZoneWebDto(1, 1, "test", "200", "P1", new ArrayList<>());
        rule5.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true));
        rule5.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false, BooleanOperateur.ET));

        ComplexRule complexRule = mapper.map(rule5, ComplexRule.class);

        Assertions.assertEquals(1, complexRule.getId());
        Assertions.assertEquals("test", complexRule.getMessage());
        Assertions.assertEquals(Priority.P1, complexRule.getPriority());
        PresenceSousZonesMemeZone simpleRule = (PresenceSousZonesMemeZone) complexRule.getFirstRule();
        Assertions.assertEquals("200", simpleRule.getZone());
        Assertions.assertEquals(2, simpleRule.getSousZoneOperators().size());
        Assertions.assertEquals("a", simpleRule.getSousZoneOperators().get(0).getSousZone());
        Assertions.assertEquals(true, simpleRule.getSousZoneOperators().get(0).isPresent());
        Assertions.assertEquals("b", simpleRule.getSousZoneOperators().get(1).getSousZone());
        Assertions.assertEquals(BooleanOperateur.ET, simpleRule.getSousZoneOperators().get(1).getOperateur());
        Assertions.assertEquals(false, simpleRule.getSousZoneOperators().get(1).isPresent());
    }

    @Test
    @DisplayName("Test ConverterIndicateur")
    void converterIndicateurTest() {
        IndicateurWebDto rule1 = new IndicateurWebDto(1, "200", "ET", 1, "#");
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule1, ComplexRule.class));
        Assertions.assertEquals("L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        IndicateurWebDto rule2 = new IndicateurWebDto(1, 1, null, "200", null, null, 1, "#");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        IndicateurWebDto rule3 = new IndicateurWebDto(1, 1, "test", "200", "P1", new ArrayList<>(), 3, "#");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("le champ indicateur peut etre soit '1', soit '2'", exception.getCause().getMessage());

        IndicateurWebDto rule4 = new IndicateurWebDto(1, 1, "test", "200", "P1", new ArrayList<>(), 1, "#");
        ComplexRule complexRule = mapper.map(rule4, ComplexRule.class);
        Assertions.assertEquals(1, complexRule.getId());
        Assertions.assertEquals("test", complexRule.getMessage());
        Indicateur simpleRule = (Indicateur) complexRule.getFirstRule();
        Assertions.assertEquals("200", simpleRule.getZone());
        Assertions.assertEquals(1, simpleRule.getIndicateur());
        Assertions.assertEquals("#", simpleRule.getValeur());
    }

    @Test
    @DisplayName("test converterNombreCaracteres")
    void converterNombreCaracteresTest() {
        NombreCaracteresWebDto rule1 = new NombreCaracteresWebDto(1, "200", "a", "ET", Operateur.INFERIEUR_EGAL, 1);
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule1, ComplexRule.class));
        Assertions.assertEquals("L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        NombreCaracteresWebDto rule2 = new NombreCaracteresWebDto(1, 1, null, "200", "a", null, null, Operateur.INFERIEUR_EGAL, 1);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        NombreCaracteresWebDto rule3 = new NombreCaracteresWebDto(1, 1, "test", "200", "a", "P1", new ArrayList<>(), Operateur.INFERIEUR, 2);
        ComplexRule complexRule = mapper.map(rule3, ComplexRule.class);
        Assertions.assertEquals(1, complexRule.getId());
        Assertions.assertEquals("test", complexRule.getMessage());
        NombreCaracteres simpleRule = (NombreCaracteres) complexRule.getFirstRule();
        Assertions.assertEquals("200", simpleRule.getZone());
        Assertions.assertEquals("a", simpleRule.getSousZone());
        Assertions.assertEquals(2, simpleRule.getOccurrences());
        Assertions.assertEquals(Operateur.INFERIEUR, simpleRule.getOperateur());
    }

    @Test
    @DisplayName("Test Mapper converterComplexRule")
    void converterComplexRuleTest() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200","ET",true));

        ComplexRuleWebDto finalComplexRuleWebDto = complexRuleWebDto;
        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(finalComplexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("La première règle d'une règle complexe ne doit pas contenir d'opérateur", exception.getCause().getMessage());


        complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        List<String> listTypeDoc = new ArrayList<>();
        listTypeDoc.add("BD");
        listTypeDoc.add("A");
        listTypeDoc.add("B");
        complexRuleWebDto.setTypesDoc(listTypeDoc);
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200",null,true));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(2,"210","ET",false));

        ComplexRule complexRule = mapper.map(complexRuleWebDto, ComplexRule.class);

        Assertions.assertEquals(complexRuleWebDto.getId(),complexRule.getId());
        Assertions.assertEquals(complexRuleWebDto.getMessage(),complexRule.getMessage());
        Assertions.assertEquals(complexRuleWebDto.getPriority(),complexRule.getPriority().toString());
        Assertions.assertEquals(complexRuleWebDto.getTypesDoc().size(),complexRule.getFamillesDocuments().size());
        Assertions.assertEquals(complexRuleWebDto.getRegles().size(),complexRule.getOtherRules().size() + 1);

        complexRuleWebDto.addRegle(new PresenceZoneWebDto(3,"300",null,true));
        ComplexRuleWebDto finalComplexRuleWebDto1 = complexRuleWebDto;
        exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(finalComplexRuleWebDto1, ComplexRule.class));
        Assertions.assertEquals("Les règles autres que la première d'une règle complexe doivent avoir un opérateur", exception.getCause().getMessage());
    }

    /**
     * Test du mapper converterResultAnalyseTest
     */
    @Test
    @DisplayName("Test Mapper converterResultAnalyseTest")
    void converterResultAnalyseTest() {
        //  Préparation d'un objet ResultAnalyse
        ResultAnalyse resultAnalyse = new ResultAnalyse();
        ResultRules resultRules = new ResultRules("123456789");
        resultRules.setTitre("Titre test");
        resultRules.setAuteur("Auteur test");
        resultRules.setIsbn("Isbn test");
        resultRules.setOcn("Ocn test");
        resultRules.setDateModification("31/12/2021");
        resultRules.setRcr("341725201");
        resultRules.setFamilleDocument(new FamilleDocument("A", "Monographie"));
        resultRules.addDetailErreur(new ResultRule(1, Priority.P1,"Message TEST"));
        ResultRule resultRule2 = new ResultRule(2, Priority.P2,"Message TEST2");
        resultRule2.addZone("200");
        resultRules.addDetailErreur(resultRule2);
        resultAnalyse.addResultRule(resultRules);
        resultAnalyse.addPpnAnalyse("4");
        resultAnalyse.addPpnErrone("1");
        resultAnalyse.addPpnOk("3");
        resultAnalyse.addPpnInconnu("0");

        //  Appel du mapper
        ResultAnalyseResponseDto responseDto = mapper.map(resultAnalyse, ResultAnalyseResponseDto.class);

        //  Contrôle de la bonne conformité des résultats
        Assertions.assertEquals(resultAnalyse.getResultRules().get(0).getPpn(), responseDto.getResultRules().get(0).getPpn());
        Assertions.assertEquals(resultAnalyse.getResultRules().get(0).getFamilleDocument().getLibelle(), responseDto.getResultRules().get(0).getTypeDocument());
        Assertions.assertEquals(resultAnalyse.getPpnAnalyses().size(), responseDto.getNbPpnAnalyses());
        Assertions.assertEquals(resultAnalyse.getPpnErrones().size(), responseDto.getNbPpnErrones());
        Assertions.assertEquals(resultAnalyse.getPpnOk().size(), responseDto.getNbPpnOk());
        Assertions.assertEquals(resultAnalyse.getPpnInconnus().size(), responseDto.getNbPpnInconnus());

        Assertions.assertEquals(resultRules.getTitre(), responseDto.getResultRules().get(0).getTitre());
        Assertions.assertEquals(resultRules.getAuteur(), responseDto.getResultRules().get(0).getAuteur());
        Assertions.assertEquals(resultRules.getIsbn(), responseDto.getResultRules().get(0).getIsbn());
        Assertions.assertEquals(resultRules.getOcn(), responseDto.getResultRules().get(0).getOcn());
        Assertions.assertEquals(resultRules.getRcr(), responseDto.getResultRules().get(0).getRcr());
        Assertions.assertEquals(resultRules.getDateModification(), responseDto.getResultRules().get(0).getDateModification());

        Assertions.assertEquals(2,responseDto.getResultRules().get(0).getDetailerreurs().size());
        Assertions.assertEquals("Message TEST",responseDto.getResultRules().get(0).getDetailerreurs().stream().filter(ruleResponseDto -> ruleResponseDto.getId() == 1).findFirst().get().getMessage());
        Assertions.assertEquals("Message TEST2",responseDto.getResultRules().get(0).getDetailerreurs().stream().filter(ruleResponseDto -> ruleResponseDto.getId() == 2).findFirst().get().getMessage());
        Assertions.assertEquals(Priority.P1.toString(),responseDto.getResultRules().get(0).getDetailerreurs().stream().filter(ruleResponseDto -> ruleResponseDto.getId() == 1).findFirst().get().getPriority());
        Assertions.assertEquals(Priority.P2.toString(),responseDto.getResultRules().get(0).getDetailerreurs().stream().filter(ruleResponseDto -> ruleResponseDto.getId() == 2).findFirst().get().getPriority());

    }


}
