package fr.abes.qualimarc.web.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.DependencyRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.LinkedRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.*;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance.Reciprocite;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.*;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRule;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.utils.*;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.dto.RuleResponseDto;
import fr.abes.qualimarc.web.dto.RuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.DependencyWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.*;
import fr.abes.qualimarc.web.dto.indexrules.dependance.ReciprociteWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import fr.abes.qualimarc.web.dto.rulesets.RuleSetWebDto;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
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

        Optional<ResultRulesResponseDto> first1 = responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("111111111")).findFirst();
        Optional<ResultRulesResponseDto> first2 = responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("888888888")).findFirst();
        Optional<ResultRulesResponseDto> first3 = responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("777777777")).findFirst();

        Assertions.assertEquals(3, responseDto.getResultRules().size());
        Assertions.assertTrue(first1.isPresent());
        Assertions.assertTrue(first2.isPresent());
        Assertions.assertTrue(first3.isPresent());
        Assertions.assertEquals(2, first1.get().getMessages().size());
        Assertions.assertEquals(3, first2.get().getMessages().size());
        Assertions.assertEquals(1, first3.get().getMessages().size());
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
        List<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        PresenceZoneWebDto presenceZoneWebDto = new PresenceZoneWebDto(1, 1, ruleSetsList, "message 1", "100", "P1", typeDoc, typeThese, true);

        //  Appel du mapper
        ComplexRule complexRule = mapper.map(presenceZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PresenceZone firstRule = (PresenceZone) complexRule.getFirstRule();
        Assertions.assertEquals(presenceZoneWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(presenceZoneWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(presenceZoneWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(presenceZoneWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertEquals(presenceZoneWebDto.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(presenceZoneWebDto.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(presenceZoneWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(presenceZoneWebDto.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
        Assertions.assertEquals(presenceZoneWebDto.getId(), firstRule.getId());
        Assertions.assertEquals(presenceZoneWebDto.getZone(), firstRule.getZone());
        Assertions.assertEquals(presenceZoneWebDto.isPresent(), firstRule.isPresent());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceZoneWebDto(1, 1, ruleSetsList,"message 1", "100", null, typeDoc, new ArrayList<>(), true), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceZoneWebDto(1, 1, ruleSetsList, null, "100", "P1", typeDoc, new ArrayList<>(), true), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

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
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        PresenceSousZoneWebDto presenceSousZoneWebDto = new PresenceSousZoneWebDto(1, 1, ruleSetsList, "message 1", "100", "P1", typeDoc, typeThese, "a", true);

        //  Appel du mapper
        ComplexRule complexRule = mapper.map(presenceSousZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PresenceSousZone firstRule = (PresenceSousZone) complexRule.getFirstRule();
        Assertions.assertEquals(presenceSousZoneWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(presenceSousZoneWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertEquals(presenceSousZoneWebDto.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(presenceSousZoneWebDto.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(presenceSousZoneWebDto.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
        Assertions.assertEquals(presenceSousZoneWebDto.getSousZone(), firstRule.getSousZone());
        Assertions.assertEquals(presenceSousZoneWebDto.getId(), firstRule.getId());
        Assertions.assertEquals(presenceSousZoneWebDto.getZone(), firstRule.getZone());
        Assertions.assertEquals(presenceSousZoneWebDto.isPresent(), firstRule.isPresent());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceSousZoneWebDto(1, 1, ruleSetsList, "message 1", "100", null, typeDoc, new ArrayList<>(), "a", true), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceSousZoneWebDto(1, 1, ruleSetsList, null, "100", "P1", typeDoc, new ArrayList<>(), "a", true), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

    }

    /**
     * Test du mapper converterNombreZone
     */
    @Test
    @DisplayName("Test Mapper converterNombreZone")
    void converterNombreZoneTest() {
        NombreZoneWebDto nombreZoneWebDto0 = new NombreZoneWebDto(1, 1, new ArrayList<>(),"test", "100", "P1", null, null, ComparaisonOperateur.INFERIEUR_EGAL, 1);
        Exception ex = Assertions.assertThrows(MappingException.class, () -> mapper.map(nombreZoneWebDto0, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle", ex.getCause().getMessage());

        NombreZoneWebDto nombreZoneWebDto1 = new NombreZoneWebDto(1, 1, new ArrayList<>(), "test", "100", "P1", null, null, ComparaisonOperateur.SUPERIEUR_EGAL, 1);
        ex = Assertions.assertThrows(MappingException.class, () -> mapper.map(nombreZoneWebDto1, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle", ex.getCause().getMessage());

        //  Préparation d'un objet NombreZoneWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        NombreZoneWebDto nombreZoneWebDto = new NombreZoneWebDto(1, 1, ruleSetsList, "message 1", "100", "P1", typeDoc, typeThese, ComparaisonOperateur.EGAL, 1);

        //  Appel du mapper
        ComplexRule complexRule = mapper.map(nombreZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        NombreZone firstRule = (NombreZone) complexRule.getFirstRule();
        Assertions.assertEquals(nombreZoneWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(nombreZoneWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(nombreZoneWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(nombreZoneWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertEquals(nombreZoneWebDto.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(nombreZoneWebDto.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(nombreZoneWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(nombreZoneWebDto.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
        Assertions.assertEquals(nombreZoneWebDto.getComparaisonOperateur(), firstRule.getComparaisonOperateur());
        Assertions.assertEquals(nombreZoneWebDto.getOccurrences(), firstRule.getOccurrences());
        Assertions.assertEquals(nombreZoneWebDto.getId(), firstRule.getId());
        Assertions.assertEquals(nombreZoneWebDto.getZone(), firstRule.getZone());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreZoneWebDto(1, 1, ruleSetsList, "message 1", "100", null, typeDoc, new ArrayList<>(), ComparaisonOperateur.SUPERIEUR, 1), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreZoneWebDto(1, 1, ruleSetsList, null, "100", "P1", typeDoc, new ArrayList<>(), ComparaisonOperateur.SUPERIEUR, 1), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

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
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        NombreSousZoneWebDto nombreSousZoneWebDto = new NombreSousZoneWebDto(1, 1, ruleSetsList, "message 1", "100", "P1", typeDoc, typeThese, "a", "100", "a");

        //  Appel du mapper
        ComplexRule complexRule = mapper.map(nombreSousZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        NombreSousZone firstRule = (NombreSousZone) complexRule.getFirstRule();
        Assertions.assertEquals(nombreSousZoneWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(nombreSousZoneWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertEquals(nombreSousZoneWebDto.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(nombreSousZoneWebDto.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(nombreSousZoneWebDto.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
        Assertions.assertEquals(nombreSousZoneWebDto.getSousZone(), firstRule.getSousZone());
        Assertions.assertEquals(nombreSousZoneWebDto.getZoneCible(), firstRule.getZoneCible());
        Assertions.assertEquals(nombreSousZoneWebDto.getSousZoneCible(), firstRule.getSousZoneCible());
        Assertions.assertEquals(nombreSousZoneWebDto.getId(), firstRule.getId());
        Assertions.assertEquals(nombreSousZoneWebDto.getZone(), firstRule.getZone());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreSousZoneWebDto(1, 1, ruleSetsList, "message 1", "100", null, typeDoc, new ArrayList<>(), "a", "200", "b"), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreSousZoneWebDto(1, 1, ruleSetsList, null, "100", "P1", typeDoc, new ArrayList<>(), "a", "200", "b"), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

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
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        PositionSousZoneWebDto positionSousZoneWebDto = new PositionSousZoneWebDto(1, 1, ruleSetsList, "message 1", "100", "P1", typeDoc, typeThese, "a", 1);

        //  Appel du mapper
        ComplexRule complexRule = mapper.map(positionSousZoneWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PositionSousZone firstRule = (PositionSousZone) complexRule.getFirstRule();
        Assertions.assertEquals(positionSousZoneWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(positionSousZoneWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(positionSousZoneWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(positionSousZoneWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertEquals(positionSousZoneWebDto.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(positionSousZoneWebDto.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(positionSousZoneWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(positionSousZoneWebDto.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
        Assertions.assertEquals(positionSousZoneWebDto.getSousZone(), firstRule.getSousZone());
        Assertions.assertEquals(positionSousZoneWebDto.getPosition(), firstRule.getPosition());
        Assertions.assertEquals(positionSousZoneWebDto.getId(), firstRule.getId());
        Assertions.assertEquals(positionSousZoneWebDto.getZone(), firstRule.getZone());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PositionSousZoneWebDto(1, 1, ruleSetsList, "message 1", "100", null, typeDoc, new ArrayList<>(), "a", 2), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PositionSousZoneWebDto(1, 1, ruleSetsList, null, "100", "P1", typeDoc, new ArrayList<>(), "a", 2), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

    }

    @Test
    @DisplayName("Test mapper converterPresenceSousZonesMemeZone")
    void converterPresenceSousZonesMemeZoneTest() {
        PresenceSousZonesMemeZoneWebDto rule1 = new PresenceSousZonesMemeZoneWebDto(1, "200", "ET");
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule1, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule2 = new PresenceSousZonesMemeZoneWebDto(1, 1, new ArrayList<>(), null, "200", null, null, null, null);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule3 = new PresenceSousZonesMemeZoneWebDto(1, 1,  new ArrayList<>(), "test", "200", "P1", new ArrayList<>(), new ArrayList<>(), new LinkedList<>());
        rule3.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true, BooleanOperateur.ET));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("La règle 1 doit avoir au moins deux sous-zones déclarées", exception.getCause().getMessage());

        rule3.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false, BooleanOperateur.ET));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : La première sous-zone ne doit pas avoir d'opérateur booléen", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule4 = new PresenceSousZonesMemeZoneWebDto(1, 1,  new ArrayList<>(), "test", "200", "P1", new ArrayList<>(), new ArrayList<>(), new LinkedList<>());
        rule4.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true));
        rule4.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule4, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Les sous-zones en dehors de la première doivent avoir un opérateur booléen", exception.getCause().getMessage());

        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        PresenceSousZonesMemeZoneWebDto rule5 = new PresenceSousZonesMemeZoneWebDto(1, 1, ruleSetsList, "test", "200", "P1", typeDoc, typeThese, new LinkedList<>());
        rule5.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true));
        rule5.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false, BooleanOperateur.ET));

        ComplexRule complexRule = mapper.map(rule5, ComplexRule.class);

        Assertions.assertEquals(rule5.getId(), complexRule.getId());
        Assertions.assertEquals(rule5.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(rule5.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(rule5.getPriority(), complexRule.getPriority().toString());
        PresenceSousZonesMemeZone firstRule = (PresenceSousZonesMemeZone) complexRule.getFirstRule();
        Assertions.assertEquals(rule5.getZone(), firstRule.getZone());
        Assertions.assertEquals(2, firstRule.getSousZoneOperators().size());
        Assertions.assertEquals("a", firstRule.getSousZoneOperators().get(0).getSousZone());
        Assertions.assertTrue(firstRule.getSousZoneOperators().get(0).isPresent());
        Assertions.assertEquals("b", firstRule.getSousZoneOperators().get(1).getSousZone());
        Assertions.assertEquals(BooleanOperateur.ET, firstRule.getSousZoneOperators().get(1).getOperateur());
        Assertions.assertFalse(firstRule.getSousZoneOperators().get(1).isPresent());
        Assertions.assertEquals(rule5.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(rule5.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(rule5.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(rule5.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
    }

    @Test
    @DisplayName("Test ConverterIndicateur")
    void converterIndicateurTest() {
        IndicateurWebDto rule1 = new IndicateurWebDto(1, "200", "ET", 1, "#", "STRICTEMENT");
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule1, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        IndicateurWebDto rule2 = new IndicateurWebDto(1, 1, new ArrayList<>(), null, "200", null, null, null, 1, "#", "STRICTEMENT");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        IndicateurWebDto rule3 = new IndicateurWebDto(1, 1, new ArrayList<>(), "test", "200", "P1", new ArrayList<>(), new ArrayList<>(), 3, "#", "STRICTEMENT");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : le champ indicateur peut etre soit '1', soit '2'", exception.getCause().getMessage());

        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        IndicateurWebDto rule4 = new IndicateurWebDto(1, 1, ruleSetsList, "test", "200", "P1", typeDoc, typeThese, 1, "#", "STRICTEMENT");

        ComplexRule complexRule = mapper.map(rule4, ComplexRule.class);
        Assertions.assertEquals(rule4.getId(), complexRule.getId());
        Assertions.assertEquals(rule4.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(rule4.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(rule4.getPriority(), complexRule.getPriority().toString());
        Indicateur simpleRule = (Indicateur) complexRule.getFirstRule();
        Assertions.assertEquals(rule4.getZone(), simpleRule.getZone());
        Assertions.assertEquals(simpleRule.getIndicateur(), simpleRule.getIndicateur());
        Assertions.assertEquals(simpleRule.getValeur(), simpleRule.getValeur());
        Assertions.assertEquals(rule4.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(rule4.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(rule4.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(rule4.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
    }

    @Test
    @DisplayName("Test ConverterTypeCaractere")
    void converterTypeCaractereTest() {
        List<String> typeCaracteres = new ArrayList<>();
        typeCaracteres.add("ALPHABETIQUE");
        TypeCaractereWebDto rule1 = new TypeCaractereWebDto(1, "200", "ET", "a", typeCaracteres);
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule1, ComplexRule.class));

        Assertions.assertEquals("Règle 1 : L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        TypeCaractereWebDto rule2 = new TypeCaractereWebDto(1, 1, new ArrayList<>(), null, "200", null, null, null, "a", typeCaracteres);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        TypeCaractereWebDto rule3 = new TypeCaractereWebDto(1, 1, new ArrayList<>(), "test", "200", "P1", new ArrayList<>(), new ArrayList<>(), "a", new ArrayList<>());
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le champ type-caracteres est obligatoire", exception.getCause().getMessage());

        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        typeCaracteres.add("NUMERIQUE");
        TypeCaractereWebDto rule6 = new TypeCaractereWebDto(1, 1,ruleSetsList, "test", "200", "P1", new ArrayList<>(), new ArrayList<>(), "a", typeCaracteres);

        ComplexRule complexRule = mapper.map(rule6, ComplexRule.class);
        TypeCaractere simpleRule = (TypeCaractere) complexRule.getFirstRule();
        Assertions.assertEquals(rule6.getId(), complexRule.getId());
        Assertions.assertEquals(rule6.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(rule6.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(rule6.getZone(), simpleRule.getZone());
        Assertions.assertEquals(rule6.getSousZone(), simpleRule.getSousZone());
        Assertions.assertTrue(simpleRule.getTypeCaracteres().contains(TypeCaracteres.ALPHABETIQUE));
        Assertions.assertTrue(simpleRule.getTypeCaracteres().contains(TypeCaracteres.NUMERIQUE));
    }

    @Test
    @DisplayName("test converterNombreCaracteres")
    void converterNombreCaracteresTest() {
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        NombreCaracteresWebDto ruleWebDto = new NombreCaracteresWebDto(1, "200", "a", "ET", ComparaisonOperateur.INFERIEUR_EGAL, 1);
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(ruleWebDto, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        NombreCaracteresWebDto ruleWebDto2 = new NombreCaracteresWebDto(1, 1, ruleSetsList, null, "200", "a", null, null, new ArrayList<>(), ComparaisonOperateur.INFERIEUR_EGAL, 1);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(ruleWebDto2, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");

        NombreCaracteresWebDto ruleWebDto3 = new NombreCaracteresWebDto(1, 1, null, "test", "200", "a", "P1", typeDoc, typeThese, ComparaisonOperateur.INFERIEUR, 2);
        ComplexRule complexRule = mapper.map(ruleWebDto3, ComplexRule.class);
        NombreCaracteres simpleRule = (NombreCaracteres) complexRule.getFirstRule();

        Assertions.assertEquals(ruleWebDto3.getId(), complexRule.getId());
        Assertions.assertEquals(ruleWebDto3.getMessage(), complexRule.getMessage());
        Assertions.assertNull(complexRule.getRuleSet());
        Assertions.assertEquals(ruleWebDto3.getZone(), simpleRule.getZone());
        Assertions.assertEquals(ruleWebDto3.getSousZone(), simpleRule.getSousZone());
        Assertions.assertEquals(ruleWebDto3.getOccurrences(), simpleRule.getOccurrences());
        Assertions.assertEquals(ruleWebDto3.getComparaisonOperateur(), simpleRule.getComparaisonOperateur());
        Assertions.assertEquals(ruleWebDto3.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(ruleWebDto3.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(ruleWebDto3.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(ruleWebDto3.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
    }

    @Test
    @DisplayName("Test Mapper converterPresenceChaineCaracteres")
    void converterPresenceChaineCaracteresTest() {
        //  Préparation d'un objet PresenceChaineCaracteresWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");

        List<Integer> ruleSetsList = new ArrayList<>();
        ruleSetsList.add(1);

        PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto chaineCaracteresWebDto1 = new PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto("Texte");
        PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto chaineCaracteresWebDto2 = new PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto("OU", "Texte");
        List<PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto> chaineCaracteresWebDtoList = new ArrayList<>();
        chaineCaracteresWebDtoList.add(chaineCaracteresWebDto1);
        chaineCaracteresWebDtoList.add(chaineCaracteresWebDto2);
        PresenceChaineCaracteresWebDto presenceChaineCaracteresWebDto = new PresenceChaineCaracteresWebDto(1, 1, ruleSetsList, "Erreur", "200", "P1", typeDoc, new ArrayList<>(), "a", "STRICTEMENT", chaineCaracteresWebDtoList);


        //  Appel du mapper
        ComplexRule complexRule = mapper.map(presenceChaineCaracteresWebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PresenceChaineCaracteres simpleRule = (PresenceChaineCaracteres) complexRule.getFirstRule();
        List<ChaineCaracteres> sortedList = simpleRule.getListChainesCaracteres().stream().sorted(Comparator.comparing(ChaineCaracteres::getPosition)).collect(Collectors.toList());
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals((presenceChaineCaracteresWebDto.getZone() + "$" + presenceChaineCaracteresWebDto.getSousZone()), complexRule.getZonesFromChildren().get(0));
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().anyMatch(familleDocument -> familleDocument.getId().equals(presenceChaineCaracteresWebDto.getTypesDoc().get(0))));
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getSousZone(), simpleRule.getSousZone());
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getTypeDeVerification(), simpleRule.getTypeDeVerification().toString());
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getListChaineCaracteres().get(0).getChaineCaracteres(), sortedList.get(0).getChaineCaracteres());
        Assertions.assertEquals(0, sortedList.get(0).getPosition());
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getListChaineCaracteres().get(1).getOperateur(), sortedList.get(1).getBooleanOperateur().toString());
        Assertions.assertEquals(1, sortedList.get(1).getPosition());
        Assertions.assertEquals(presenceChaineCaracteresWebDto.getListChaineCaracteres().get(1).getChaineCaracteres(), sortedList.get(1).getChaineCaracteres());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceChaineCaracteresWebDto(1, 1, ruleSetsList, "Erreur", "200", null, typeDoc, new ArrayList<>(), "a", "STRICTEMENT", chaineCaracteresWebDtoList), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceChaineCaracteresWebDto(1, 1, ruleSetsList, null, "200", "P1", typeDoc, new ArrayList<>(), "a", "STRICTEMENT", chaineCaracteresWebDtoList), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Test mapper converterComparaisonContenuSousZone")
    void converterComparaisonContenuSousZoneTest(){
        //  Préparation d'un objet ComparaisonContenuSousZone
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");

        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        ComparaisonContenuSousZoneWebDto rule1WebDto = new ComparaisonContenuSousZoneWebDto(1, 1, ruleSetsList, "Erreur", "200", "P1", typeDoc, new ArrayList<>(), "a", "STRICTEMENTDIFFERENT", "1", "300", "b");

        //  Appel du mapper
        ComplexRule complexRule = mapper.map(rule1WebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        ComparaisonContenuSousZone simpleRule = (ComparaisonContenuSousZone) complexRule.getFirstRule();

        Assertions.assertEquals(rule1WebDto.getId(), complexRule.getId());
        Assertions.assertEquals(rule1WebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(rule1WebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(2, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals(rule1WebDto.getZone() + "$" + rule1WebDto.getSousZone(), complexRule.getZonesFromChildren().get(0));
        Assertions.assertEquals(rule1WebDto.getZoneCible() + "$" + rule1WebDto.getSousZoneCible(), complexRule.getZonesFromChildren().get(1));
        Assertions.assertEquals(rule1WebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().anyMatch(familleDocument -> familleDocument.getId().equals(rule1WebDto.getTypesDoc().get(0))));
        Assertions.assertEquals(rule1WebDto.getZone(), complexRule.getFirstRule().getZone());
        Assertions.assertEquals(rule1WebDto.getSousZone(), simpleRule.getSousZone());
        Assertions.assertEquals(rule1WebDto.getTypeVerification(), ((ComparaisonContenuSousZone) complexRule.getFirstRule()).getTypeVerification().toString());
        Assertions.assertEquals(rule1WebDto.getNombreCaracteres(), ((ComparaisonContenuSousZone) complexRule.getFirstRule()).getNombreCaracteres().toString());
        Assertions.assertEquals(rule1WebDto.getZoneCible(), ((ComparaisonContenuSousZone) complexRule.getFirstRule()).getZoneCible());
        Assertions.assertEquals(rule1WebDto.getSousZoneCible(), ((ComparaisonContenuSousZone) complexRule.getFirstRule()).getSousZoneCible());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new ComparaisonContenuSousZoneWebDto(1, 1, ruleSetsList, "Erreur", "200", null, typeDoc, new ArrayList<>(), "a", "300", "b", "STRICTEMENTDIFFERENT"), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new ComparaisonContenuSousZoneWebDto(1, 1, ruleSetsList, null, "200", "P1", typeDoc, new ArrayList<>(), "a", "300", "b", "STRICTEMENTDIFFERENT"), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Test Mapper converterComparaisonDateTest")
    void converterComparaisonDate(){
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");

        List<Integer> ruleSetsList = new ArrayList<>();
        ruleSetsList.add(1);

        ComparaisonDateWebDto comparaisonDateWebDto = new ComparaisonDateWebDto(1,1,ruleSetsList,"MESSAGE","200","P1",typeDoc, null,"a",0,4,"400","a",0,4,"EGAL");
        ComplexRule complexRule = mapper.map(comparaisonDateWebDto, ComplexRule.class);

        ComparaisonDate comparaisonDate = (ComparaisonDate) complexRule.getFirstRule();

        Assertions.assertEquals(comparaisonDateWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(comparaisonDateWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(comparaisonDateWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(2, complexRule.getZonesFromChildren().size());
        Assertions.assertEquals(comparaisonDateWebDto.getZone() + "$" + comparaisonDateWebDto.getSousZone(), complexRule.getZonesFromChildren().get(0));
        Assertions.assertEquals(comparaisonDateWebDto.getZoneCible() + "$" + comparaisonDateWebDto.getSousZoneCible(), complexRule.getZonesFromChildren().get(1));
        Assertions.assertEquals(comparaisonDateWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().anyMatch(familleDocument -> familleDocument.getId().equals(comparaisonDateWebDto.getTypesDoc().get(0))));
        Assertions.assertEquals(comparaisonDateWebDto.getSousZone(), comparaisonDate.getSousZone());
        Assertions.assertEquals(comparaisonDateWebDto.getZone(), comparaisonDate.getZone());
    }

    @Test
    @DisplayName("Test Mapper converterTypeDocumentTest")
    void converterTypeDocument(){
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");

        List<Integer> ruleSetsList = new ArrayList<>();
        ruleSetsList.add(1);

        TypeDocumentWebDto typeDocumentWebDto = new TypeDocumentWebDto(1,1,ruleSetsList,"MESSAGE","P1",typeDoc, null,"STRICTEMENT",1,"A");
        ComplexRule complexRule = mapper.map(typeDocumentWebDto, ComplexRule.class);

        TypeDocument typeDocument = (TypeDocument) complexRule.getFirstRule();

        Assertions.assertEquals(typeDocumentWebDto.getId(), complexRule.getId());
        Assertions.assertEquals(typeDocumentWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(typeDocumentWebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals(typeDocumentWebDto.getZone(), complexRule.getZonesFromChildren().get(0));
        Assertions.assertEquals(typeDocumentWebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().anyMatch(familleDocument -> familleDocument.getId().equals(typeDocumentWebDto.getTypesDoc().get(0))));
        Assertions.assertEquals(typeDocumentWebDto.getPosition(), typeDocument.getPosition());
        Assertions.assertEquals(typeDocumentWebDto.getTypeDeVerification(), typeDocument.getTypeDeVerification().toString());
        Assertions.assertEquals(typeDocumentWebDto.getValeur(), typeDocument.getValeur());
        Assertions.assertEquals(typeDocumentWebDto.getZone(), typeDocument.getZone());

        typeDocumentWebDto.setPosition(null);
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(typeDocumentWebDto, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : le champ position est obligatoire", exception.getCause().getMessage());

        typeDocumentWebDto.setPosition(0);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(typeDocumentWebDto, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : le champ position ne peut être compris qu'entre 1 et 4", exception.getCause().getMessage());

        typeDocumentWebDto.setPosition(1);
        typeDocumentWebDto.setTypeDeVerification("");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(typeDocumentWebDto, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : le champ type-de-verification est obligatoire", exception.getCause().getMessage());

        typeDocumentWebDto.setTypeDeVerification("");
        typeDocumentWebDto.setValeur("");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(typeDocumentWebDto, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : le champ valeur est obligatoire", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("règle complexe : cas ou la règle complexe contient des règles simples avec zones génériques")
    void converterComplexRuleWithGenericZoneInSimpleRule() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setMessage("test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1, "7XX", "ET", true));
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle complexe ne peut pas contenir de règles simple avec des zones génériques", exception.getCause().getMessage());

    }

    @Test
    @DisplayName("règle complexe : cas ou la première règle contient un opérateur")
    void converterComplexRuleWithFirstRegleWithOperator() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200","ET",true));

        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("La première règle d'une règle complexe ne doit pas contenir d'opérateur", exception.getCause().getMessage());

    }

    @Test
    @DisplayName("Règle complexe : cas où la première règle est une règle de dépendance")
    void converterComplexRuleWithFirstRegleDependance() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.addRegle(new DependencyWebDto(1,"200","a", "AUTORITE"));

        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("La première règle d'une règle complexe ne peut pas être une règle de dépendance", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Règle complexe : cas où une règle de dépendance n'est pas suivi de règles simples")
    void converterComplexRuleWithDependencyAndNoSimpleRuleAfter() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200",null,true));
        complexRuleWebDto.addRegle(new DependencyWebDto(2,"200","a","AUTORITE"));

        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance doit toujours être suivie d'une règle simple", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Règle complexe : cas où une règle de dépendance n'est pas suivi d'une règle simple sans opérateur")
    void converterComplexRuleWithDependencyAndSimpleRuleAfterWithoutOperator() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200",null,true));
        complexRuleWebDto.addRegle(new DependencyWebDto(2,"200","a","AUTORITE"));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(3,"200","ET",true));

        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle simple suivant une règle de dépendance ne doit pas avoir d'opérateur", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Test Mapper règle complexe avec règle de dépendance")
    void converterComplexRuleWithDependencyRule() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        List<String> listTypeDoc = new ArrayList<>();
        listTypeDoc.add("BD");
        listTypeDoc.add("A");
        listTypeDoc.add("B");
        complexRuleWebDto.setTypesDoc(listTypeDoc);
        List<String> listTypeThese = new ArrayList<>();
        listTypeThese.add("REPRO");
        complexRuleWebDto.setTypesThese(listTypeThese);
        List<Integer> ruleSetList= new ArrayList<>();
        ruleSetList.add(1);
        complexRuleWebDto.setRuleSetList(ruleSetList);
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200",null,true));
        complexRuleWebDto.addRegle(new DependencyWebDto(2,"200","a","AUTORITE"));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(3,"210",null,false));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(4,"220","ET",false));

        ComplexRule complexRule = mapper.map(complexRuleWebDto, ComplexRule.class);

        Assertions.assertEquals(complexRuleWebDto.getId(),complexRule.getId());
        Assertions.assertEquals(complexRuleWebDto.getMessage(),complexRule.getMessage());
        Assertions.assertEquals(complexRuleWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(complexRuleWebDto.getPriority(),complexRule.getPriority().toString());
        Assertions.assertEquals(complexRuleWebDto.getTypesDoc().size(),complexRule.getFamillesDocuments().size());
        Assertions.assertEquals(complexRuleWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertEquals(complexRuleWebDto.getRegles().size(),complexRule.getOtherRules().size() + 1);

        Assertions.assertEquals(complexRuleWebDto.getRegles().get(0).getId(), complexRule.getFirstRule().getId());
        Assertions.assertEquals(complexRuleWebDto.getRegles().get(0).getZone(), complexRule.getFirstRule().getZone());

        Assertions.assertTrue(complexRule.getOtherRules().get(0) instanceof DependencyRule);
        Assertions.assertEquals(complexRuleWebDto.getRegles().get(1).getId(), complexRule.getOtherRules().get(0).getId());
        Assertions.assertEquals(complexRuleWebDto.getRegles().get(1).getZone(), ((DependencyRule) complexRule.getOtherRules().get(0)).getZoneSource());
        Assertions.assertEquals("a", ((DependencyRule) complexRule.getOtherRules().get(0)).getSousZoneSource());
        Assertions.assertEquals(0, complexRule.getOtherRules().get(0).getPosition());

        Assertions.assertEquals(complexRuleWebDto.getRegles().get(2).getId(), complexRule.getOtherRules().get(1).getId());
        Assertions.assertEquals(1, complexRule.getOtherRules().get(1).getPosition());
    }

    @Test
    @DisplayName("Règle complexe : cas où une règle de réciprocité est placée avant une règle de dépendance")
    void converterComplexRuleWithDependencyAndReciprociteBefore() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1, "800", true));
        complexRuleWebDto.addRegle(new ReciprociteWebDto(1, "200", "ET", "a"));
        complexRuleWebDto.addRegle(new DependencyWebDto(2,"200","a","AUTORITE"));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(3,"200","ET",true));

        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance doit être créée avant de créer une règle de réciprocité", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Règle complexe : cas où une règle de réciprocité est créée seule")
    void converterComplexRuleReciprociteAlone() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new ReciprociteWebDto(1, "200", "a"));

        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("La première règle d'une règle complexe ne peut pas être une règle de réciprocité", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Règle complexe : cas où une règle de réciprocité est créée sans règle de dépendance")
    void converterComplexRuleReciprociteWithoutDependency() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1, "230", null, true));
        complexRuleWebDto.addRegle(new ReciprociteWebDto(2, "200", "a"));

        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance doit être créée avant de créer une règle de réciprocité", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Test Mapper règle complexe avec règle de dépendance et règle de réciprocité")
    void converterComplexRuleWithDependencyRuleAndReciprocite() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        List<String> listTypeDoc = new ArrayList<>();
        listTypeDoc.add("BD");
        listTypeDoc.add("A");
        listTypeDoc.add("B");
        complexRuleWebDto.setTypesDoc(listTypeDoc);
        List<String> listTypeThese = new ArrayList<>();
        listTypeThese.add("REPRO");
        complexRuleWebDto.setTypesThese(listTypeThese);
        List<Integer> ruleSetList= new ArrayList<>();
        ruleSetList.add(1);
        complexRuleWebDto.setRuleSetList(ruleSetList);
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1, "300", null, true));
        complexRuleWebDto.addRegle(new DependencyWebDto(2,"200","a","AUTORITE"));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(3,"210",null,false));
        complexRuleWebDto.addRegle(new ReciprociteWebDto(4, "200", "ET","a"));

        ComplexRule complexRule = mapper.map(complexRuleWebDto, ComplexRule.class);

        Assertions.assertEquals(complexRuleWebDto.getId(),complexRule.getId());
        Assertions.assertEquals(complexRuleWebDto.getMessage(),complexRule.getMessage());
        Assertions.assertEquals(complexRuleWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(complexRuleWebDto.getPriority(),complexRule.getPriority().toString());
        Assertions.assertEquals(complexRuleWebDto.getTypesDoc().size(),complexRule.getFamillesDocuments().size());
        Assertions.assertEquals(complexRuleWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertEquals(complexRuleWebDto.getRegles().size(),complexRule.getOtherRules().size() + 1);

        Assertions.assertEquals(complexRuleWebDto.getRegles().get(0).getId(), complexRule.getFirstRule().getId());
        Assertions.assertEquals(complexRuleWebDto.getRegles().get(0).getZone(), complexRule.getFirstRule().getZone());

        Assertions.assertTrue(complexRule.getOtherRules().get(0) instanceof DependencyRule);
        Assertions.assertEquals(complexRuleWebDto.getRegles().get(1).getId(), complexRule.getOtherRules().get(0).getId());
        Assertions.assertEquals(complexRuleWebDto.getRegles().get(1).getZone(), ((DependencyRule) complexRule.getOtherRules().get(0)).getZoneSource());
        Assertions.assertEquals("a", ((DependencyRule) complexRule.getOtherRules().get(0)).getSousZoneSource());
        Assertions.assertEquals(0, complexRule.getOtherRules().get(0).getPosition());

        Assertions.assertEquals(complexRuleWebDto.getRegles().get(2).getId(), complexRule.getOtherRules().get(1).getId());
        Assertions.assertEquals(1, complexRule.getOtherRules().get(1).getPosition());


        Assertions.assertEquals(complexRuleWebDto.getRegles().get(3).getId(), complexRule.getOtherRules().get(2).getId());
        Assertions.assertEquals(2, complexRule.getOtherRules().get(2).getPosition());
        LinkedRule linkedRule = (LinkedRule) complexRule.getOtherRules().get(2);
        Assertions.assertTrue(linkedRule.getRule() instanceof Reciprocite);
    }

    @Test
    @DisplayName("Règle complexe : teste les champs d'une règle de dépendance")
    void converterComplexRuleWithDependencyCheck() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200",null,true));
        DependencyWebDto dependencyWebDto = new DependencyWebDto(2,"200","a","AUTORITE");
        complexRuleWebDto.addRegle(dependencyWebDto);
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(3,"200",null,true));

        dependencyWebDto.setPriority("P1");
        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance ne peut pas avoir de priorité", exception.getCause().getMessage());
        dependencyWebDto.setPriority(null);

        dependencyWebDto.setMessage("test");
        exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance ne peut pas avoir de message", exception.getCause().getMessage());
        dependencyWebDto.setMessage(null);

        dependencyWebDto.setBooleanOperator("ET");
        exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance ne peut pas avoir d'opérateur", exception.getCause().getMessage());
        dependencyWebDto.setBooleanOperator(null);

        dependencyWebDto.setRuleSetList(Lists.newArrayList(1));
        exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance ne peut pas avoir de jeu de règles personnalisé", exception.getCause().getMessage());
        dependencyWebDto.setRuleSetList(Lists.newArrayList());

        dependencyWebDto.setTypesDoc(Lists.newArrayList("A"));
        exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance ne peut pas avoir de famille de documents", exception.getCause().getMessage());
        dependencyWebDto.setTypesDoc(Lists.newArrayList());

        dependencyWebDto.setTypesThese(Lists.newArrayList("REPRO"));
        exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Une règle de dépendance ne peut pas avoir de type thèse", exception.getCause().getMessage());
        dependencyWebDto.setTypesThese(Lists.newArrayList());

    }

    @Test
    @DisplayName("Test Mapper converterComplexRule")
    void converterComplexRuleTest() {
        ComplexRuleWebDto complexRuleWebDto = new ComplexRuleWebDto();
        complexRuleWebDto.setId(1);
        complexRuleWebDto.setIdExcel(1);
        complexRuleWebDto.setMessage("message test");
        complexRuleWebDto.setPriority("P1");
        List<String> listTypeDoc = new ArrayList<>();
        listTypeDoc.add("BD");
        listTypeDoc.add("A");
        listTypeDoc.add("B");
        complexRuleWebDto.setTypesDoc(listTypeDoc);
        List<String> listTypeThese = new ArrayList<>();
        listTypeThese.add("REPRO");
        complexRuleWebDto.setTypesThese(listTypeThese);
        List<Integer> ruleSetList= new ArrayList<>();
        ruleSetList.add(1);
        complexRuleWebDto.setRuleSetList(ruleSetList);
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200",null,true));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(2,"210","ET",false));

        ComplexRule complexRule = mapper.map(complexRuleWebDto, ComplexRule.class);

        Assertions.assertEquals(complexRuleWebDto.getId(),complexRule.getId());
        Assertions.assertEquals(complexRuleWebDto.getMessage(),complexRule.getMessage());
        Assertions.assertEquals(complexRuleWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(complexRuleWebDto.getPriority(),complexRule.getPriority().toString());
        Assertions.assertEquals(complexRuleWebDto.getTypesDoc().size(),complexRule.getFamillesDocuments().size());
        Assertions.assertEquals(complexRuleWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertEquals(complexRuleWebDto.getRegles().size(),complexRule.getOtherRules().size() + 1);

        complexRuleWebDto.addRegle(new PresenceZoneWebDto(3,"300",null,true));
        MappingException exception = Assertions.assertThrows(MappingException.class, ()->mapper.map(complexRuleWebDto, ComplexRule.class));
        Assertions.assertEquals("Les règles autres que la première d'une règle complexe doivent avoir un opérateur", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("test mapper ComplexRulesMemeZone")
    void converterComplexRulesMemeZoneTest() {
        // Test de la règle meme instance de zone
        ComplexRuleWebDto complexRuleWebDto1 = new ComplexRuleWebDto();
        complexRuleWebDto1.setId(1);
        complexRuleWebDto1.setIdExcel(1);
        complexRuleWebDto1.setMessage("message test");
        complexRuleWebDto1.setPriority("P1");
        complexRuleWebDto1.setZone("200");
        complexRuleWebDto1.addRegle(new PresenceZoneWebDto(2,null,true));
        complexRuleWebDto1.addRegle(new PresenceSousZoneWebDto(3,null,null,"a",true));
        complexRuleWebDto1.addRegle(new IndicateurWebDto(4,null,null,1,"#", "STRICTEMENT"));
        complexRuleWebDto1.addRegle(new PositionSousZoneWebDto(4,null,null,"a",1));

        ComplexRule complexRule = mapper.map(complexRuleWebDto1, ComplexRule.class);

        Assertions.assertEquals(complexRuleWebDto1.getId(),complexRule.getId());
        Assertions.assertEquals(complexRuleWebDto1.getMessage(),complexRule.getMessage());
        Assertions.assertEquals(complexRuleWebDto1.getPriority(),complexRule.getPriority().toString());
        Assertions.assertEquals(complexRuleWebDto1.getZone(),complexRule.getFirstRule().getZone());
        Assertions.assertEquals(complexRuleWebDto1.getRegles().size(),complexRule.getOtherRules().size() + 1);
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

        Assertions.assertEquals(resultRules.getTitre(), responseDto.getResultRules().get(0).getTitre());
        Assertions.assertEquals(resultRules.getAuteur(), responseDto.getResultRules().get(0).getAuteur());
        Assertions.assertEquals(resultRules.getIsbn(), responseDto.getResultRules().get(0).getIsbn());
        Assertions.assertEquals(resultRules.getOcn(), responseDto.getResultRules().get(0).getOcn());
        Assertions.assertEquals(resultRules.getRcr(), responseDto.getResultRules().get(0).getRcr());
        Assertions.assertEquals(resultRules.getDateModification(), responseDto.getResultRules().get(0).getDateModification());

        Assertions.assertEquals(2,responseDto.getResultRules().get(0).getDetailerreurs().size());

        Optional<RuleResponseDto> first1 = responseDto.getResultRules().get(0).getDetailerreurs().stream().filter(ruleResponseDto -> ruleResponseDto.getId() == 1).findFirst();
        Optional<RuleResponseDto> first2 = responseDto.getResultRules().get(0).getDetailerreurs().stream().filter(ruleResponseDto -> ruleResponseDto.getId() == 2).findFirst();

        Assertions.assertTrue(first1.isPresent());
        Assertions.assertTrue(first2.isPresent());
        Assertions.assertEquals("Message TEST",first1.get().getMessage());
        Assertions.assertEquals("Message TEST2",first2.get().getMessage());
        Assertions.assertEquals(Priority.P1.toString(),first1.get().getPriority());
        Assertions.assertEquals(Priority.P2.toString(),first2.get().getPriority());

        //test où la notice est une thèse
        resultRules.setTypeThese(TypeThese.REPRO);
        responseDto = mapper.map(resultAnalyse, ResultAnalyseResponseDto.class);
        Assertions.assertEquals("Thèse de reproduction", responseDto.getResultRules().get(0).getTypeDocument());
    }

    @Test
    @DisplayName("test converter ComplexRule vers RuleWebDto")
    void converterComplexRuleToRuleWebDto() {
        SimpleRule simpleRule = new PresenceZone(1, "200", true);
        ComplexRule complexRule = new ComplexRule(1, "message", Priority.P1, simpleRule);

        RuleWebDto ruleWebDto = mapper.map(complexRule, RuleWebDto.class);

        Assertions.assertEquals(1, ruleWebDto.getId());
        Assertions.assertEquals("Tous", ruleWebDto.getTypeDoc());
        Assertions.assertEquals("Essentielle", ruleWebDto.getPriority());
        Assertions.assertEquals("message", ruleWebDto.getMessage());
        Assertions.assertEquals("200", ruleWebDto.getZoneUnm1());
        Assertions.assertNull(ruleWebDto.getZoneUnm2());

        //tests génération des types de documents & des thèses
        complexRule.addTypeDocument(new FamilleDocument("A", "Monographie"));

        ruleWebDto = mapper.map(complexRule, RuleWebDto.class);
        Assertions.assertEquals("Monographie", ruleWebDto.getTypeDoc());

        complexRule.addTypeDocument(new FamilleDocument("F", "Manuscrit"));
        ruleWebDto = mapper.map(complexRule, RuleWebDto.class);
        Assertions.assertEquals("Monographie, Manuscrit", ruleWebDto.getTypeDoc());

        complexRule.addTypeThese(TypeThese.REPRO);
        ruleWebDto = mapper.map(complexRule, RuleWebDto.class);
        Assertions.assertEquals("Monographie, Manuscrit, Thèse de reproduction", ruleWebDto.getTypeDoc());

        //test avec plusieurs zones dans la règle
        complexRule.addOtherRule(new LinkedRule(new PresenceZone(2, "310", true), BooleanOperateur.ET, 1, complexRule));
        ruleWebDto = mapper.map(complexRule, RuleWebDto.class);
        Assertions.assertEquals("310", ruleWebDto.getZoneUnm2());

        complexRule = new ComplexRule(1, "message", Priority.P1, new PresenceZone(1, "310", true));
        complexRule.addOtherRule(new LinkedRule(new Indicateur(3, "310", 1, "#", TypeVerification.STRICTEMENT), BooleanOperateur.ET, 2, complexRule));
        ruleWebDto = mapper.map(complexRule, RuleWebDto.class);
        Assertions.assertEquals("310", ruleWebDto.getZoneUnm1());
        Assertions.assertNull(ruleWebDto.getZoneUnm2());
    }

    @Test
    @DisplayName("test mapper familleDocument")
    void converterFamilleDocument() {
        FamilleDocument familleDocument = new FamilleDocument("A", "Monographie");
        FamilleDocumentWebDto dto = mapper.map(familleDocument, FamilleDocumentWebDto.class);
        Assertions.assertEquals(dto.getId(), familleDocument.getId());
        Assertions.assertEquals(dto.getLibelle(), familleDocument.getLibelle());
    }

    @Test
    @DisplayName("Test mapper ruleset")
    void converterRuleSet() {
        RuleSetWebDto ruleSetWebDto = new RuleSetWebDto(1, "libellé test", "description test", 0);
        RuleSet entity = mapper.map(ruleSetWebDto, RuleSet.class);
        Assertions.assertEquals(ruleSetWebDto.getId(), entity.getId());
        Assertions.assertEquals(ruleSetWebDto.getLibelle(), entity.getLibelle());
        Assertions.assertEquals(ruleSetWebDto.getDescription(), entity.getDescription());
        Assertions.assertEquals(ruleSetWebDto.getPosition(), entity.getPosition());

        ruleSetWebDto.setPosition(null);
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(ruleSetWebDto, entity));
        Assertions.assertEquals("La position du jeu de règles est obligatoire", exception.getCause().getMessage());

        ruleSetWebDto.setLibelle(null);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(ruleSetWebDto, entity));
        Assertions.assertEquals("Le libellé du jeu de règles est obligatoire", exception.getCause().getMessage());

        ruleSetWebDto.setId(null);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(ruleSetWebDto, entity));
        Assertions.assertEquals("L'identifiant du jeu de règles est obligatoire", exception.getCause().getMessage());

    }
}