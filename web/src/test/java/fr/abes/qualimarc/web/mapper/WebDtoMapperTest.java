package fr.abes.qualimarc.web.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.Indicateur;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.NombreCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.PresenceChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.TypeCaractere;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.*;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRule;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.repository.qualimarc.RuleSetRepository;
import fr.abes.qualimarc.core.utils.*;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.dto.RuleResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.IndicateurWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.NombreCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.PresenceChaineCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.TypeCaractereWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {UtilsMapper.class, ObjectMapper.class, WebDtoMapper.class})
public class WebDtoMapperTest {

    @Autowired
    UtilsMapper mapper;

    @MockBean
    private RuleSetRepository ruleSetRepository;

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
        List<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        PresenceZoneWebDto presenceZoneWebDto = new PresenceZoneWebDto(1, 1, ruleSetsList, "message 1", "100", "P1", typeDoc, typeThese, true);

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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
        NombreZoneWebDto nombreZoneWebDto0 = new NombreZoneWebDto(1, 1, new ArrayList<>(),"test", "100", "P1", null, null, Operateur.INFERIEUR_EGAL, 1);
        Exception ex = Assertions.assertThrows(MappingException.class, () -> mapper.map(nombreZoneWebDto0, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle", ex.getCause().getMessage());

        NombreZoneWebDto nombreZoneWebDto1 = new NombreZoneWebDto(1, 1, new ArrayList<>(), "test", "100", "P1", null, null, Operateur.SUPERIEUR_EGAL, 1);
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

        NombreZoneWebDto nombreZoneWebDto = new NombreZoneWebDto(1, 1, ruleSetsList, "message 1", "100", "P1", typeDoc, typeThese, Operateur.EGAL, 1);

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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
        Assertions.assertEquals(nombreZoneWebDto.getOperateur(), firstRule.getOperateur());
        Assertions.assertEquals(nombreZoneWebDto.getOccurrences(), firstRule.getOccurrences());
        Assertions.assertEquals(nombreZoneWebDto.getId(), firstRule.getId());
        Assertions.assertEquals(nombreZoneWebDto.getZone(), firstRule.getZone());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreZoneWebDto(1, 1, ruleSetsList, "message 1", "100", null, typeDoc, new ArrayList<>(), Operateur.SUPERIEUR, 1), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new NombreZoneWebDto(1, 1, ruleSetsList, null, "100", "P1", typeDoc, new ArrayList<>(), Operateur.SUPERIEUR, 1), ComplexRule.class));
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

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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

        PresenceSousZonesMemeZoneWebDto rule2 = new PresenceSousZonesMemeZoneWebDto(1, 1, new ArrayList<>(), null, "200", null, null, null);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule3 = new PresenceSousZonesMemeZoneWebDto(1, 1,  new ArrayList<>(), "test", "200", "P1", new ArrayList<>(), new ArrayList<>());
        rule3.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true, BooleanOperateur.ET));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("La règle 1 doit avoir au moins deux sous-zones déclarées", exception.getCause().getMessage());

        rule3.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false, BooleanOperateur.ET));
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : La première sous-zone ne doit pas avoir d'opérateur booléen", exception.getCause().getMessage());

        PresenceSousZonesMemeZoneWebDto rule4 = new PresenceSousZonesMemeZoneWebDto(1, 1,  new ArrayList<>(), "test", "200", "P1", new ArrayList<>(), new ArrayList<>());
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

        PresenceSousZonesMemeZoneWebDto rule5 = new PresenceSousZonesMemeZoneWebDto(1, 1, ruleSetsList, "test", "200", "P1", typeDoc, typeThese);
        rule5.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("a", true));
        rule5.addSousZone(new PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto("b", false, BooleanOperateur.ET));

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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
        IndicateurWebDto rule1 = new IndicateurWebDto(1, "200", "ET", 1, "#");
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule1, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        IndicateurWebDto rule2 = new IndicateurWebDto(1, 1, new ArrayList<>(), null, "200", null, null, null, 1, "#");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule2, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        IndicateurWebDto rule3 = new IndicateurWebDto(1, 1, new ArrayList<>(), "test", "200", "P1", new ArrayList<>(), new ArrayList<>(), 3, "#");
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(rule3, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : le champ indicateur peut etre soit '1', soit '2'", exception.getCause().getMessage());

        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        IndicateurWebDto rule4 = new IndicateurWebDto(1, 1, ruleSetsList, "test", "200", "P1", typeDoc, typeThese, 1, "#");

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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

        //  Création du mockito
        RuleSet ruleSet = new RuleSet(1, "libellé");
        Mockito.when(ruleSetRepository.findRuleSetById(ruleSetWebDto)).thenReturn(ruleSet);

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

        NombreCaracteresWebDto ruleWebDto = new NombreCaracteresWebDto(1, "200", "a", "ET", Operateur.INFERIEUR_EGAL, 1);
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(ruleWebDto, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : L'opérateur est interdit lors de la création d'une seule règle", exception.getCause().getMessage());

        NombreCaracteresWebDto ruleWebDto2 = new NombreCaracteresWebDto(1, 1, ruleSetsList, null, "200", "a", null, null, new ArrayList<>(), Operateur.INFERIEUR_EGAL, 1);
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(ruleWebDto2, ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");
        List<String> typeThese = new ArrayList<>();
        typeThese.add("REPRO");
        
        NombreCaracteresWebDto ruleWebDto3 = new NombreCaracteresWebDto(1, 1, null, "test", "200", "a", "P1", typeDoc, typeThese, Operateur.INFERIEUR, 2);
        ComplexRule complexRule = mapper.map(ruleWebDto3, ComplexRule.class);
        NombreCaracteres simpleRule = (NombreCaracteres) complexRule.getFirstRule();

        Assertions.assertEquals(ruleWebDto3.getId(), complexRule.getId());
        Assertions.assertEquals(ruleWebDto3.getMessage(), complexRule.getMessage());
        Assertions.assertNull(complexRule.getRuleSet());
        Assertions.assertEquals(ruleWebDto3.getZone(), simpleRule.getZone());
        Assertions.assertEquals(ruleWebDto3.getSousZone(), simpleRule.getSousZone());
        Assertions.assertEquals(ruleWebDto3.getOccurrences(), simpleRule.getOccurrences());
        Assertions.assertEquals(ruleWebDto3.getOperateur(), simpleRule.getOperateur());
        Assertions.assertEquals(ruleWebDto3.getTypesDoc().size(), complexRule.getFamillesDocuments().size());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().findFirst().isPresent());
        Assertions.assertEquals(ruleWebDto3.getTypesDoc().get(0), complexRule.getFamillesDocuments().stream().findFirst().get().getId());
        Assertions.assertEquals(ruleWebDto3.getTypesThese().size(), complexRule.getTypesThese().size());
        Assertions.assertTrue(complexRule.getTypesThese().stream().findFirst().isPresent());
        Assertions.assertEquals(ruleWebDto3.getTypesThese().get(0), complexRule.getTypesThese().stream().findFirst().get().toString());
    }

    @Test
    @DisplayName("Test Mapper converterPresenceChaineCaracteresTest")
    void converterPresenceChaineCaracteres() {
        //  Préparation d'un objet PresenceChaineCaracteresWebDto
        ArrayList<String> typeDoc = new ArrayList<>();
        typeDoc.add("A");

        List<Integer> ruleSetsList = new ArrayList<>();
        Integer ruleSetWebDto = 1;
        ruleSetsList.add(ruleSetWebDto);

        PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto chaineCaracteresWebDto1 = new PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto("Texte");
        PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto chaineCaracteresWebDto2 = new PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto("OU", "Texte");
        PresenceChaineCaracteresWebDto rule1WebDto = new PresenceChaineCaracteresWebDto(1, 1, ruleSetsList, "Erreur", "200", "P1", typeDoc, new ArrayList<>(), "a", "STRICTEMENT");
        rule1WebDto.addChaineCaracteres(chaineCaracteresWebDto1);
        rule1WebDto.addChaineCaracteres(chaineCaracteresWebDto2);

        //  Création du Mockito https://en.wikipedia.org/wiki/Mockito
        Mockito.when(ruleSetRepository.findRuleSetById(1)).thenReturn(new RuleSet(1,"libellé"));

        //  Appel du mapper
        ComplexRule complexRule = mapper.map(rule1WebDto, ComplexRule.class);

        //  Contrôle de la bonne conformité des résultats
        PresenceChaineCaracteres simpleRule = (PresenceChaineCaracteres) complexRule.getFirstRule();
        List<ChaineCaracteres> sortedList = simpleRule.getListChainesCaracteres().stream().sorted(Comparator.comparing(ChaineCaracteres::getPosition)).collect(Collectors.toList());
        Assertions.assertEquals(rule1WebDto.getId(), complexRule.getId());
        Assertions.assertEquals(rule1WebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(rule1WebDto.getMessage(), complexRule.getMessage());
        Assertions.assertEquals((rule1WebDto.getZone() + "$" + rule1WebDto.getSousZone()), complexRule.getZonesFromChildren().get(0));
        Assertions.assertEquals(rule1WebDto.getPriority(), complexRule.getPriority().toString());
        Assertions.assertTrue(complexRule.getFamillesDocuments().stream().anyMatch(familleDocument -> familleDocument.getId().equals(rule1WebDto.getTypesDoc().get(0))));
        Assertions.assertEquals(rule1WebDto.getSousZone(), simpleRule.getSousZone());
        Assertions.assertEquals(rule1WebDto.getTypeDeVerification(), simpleRule.getEnumTypeDeVerification().toString());
        Assertions.assertEquals(rule1WebDto.getListChaineCaracteres().get(0).getChaineCaracteres(), sortedList.get(0).getChaineCaracteres());
        Assertions.assertEquals(0, sortedList.get(0).getPosition());
        Assertions.assertEquals(rule1WebDto.getListChaineCaracteres().get(1).getOperateur(), sortedList.get(1).getBooleanOperateur().toString());
        Assertions.assertEquals(1, sortedList.get(1).getPosition());
        Assertions.assertEquals(rule1WebDto.getListChaineCaracteres().get(1).getChaineCaracteres(), sortedList.get(1).getChaineCaracteres());

        //  Test avec priorité nulle
        MappingException exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceChaineCaracteresWebDto(1, 1, ruleSetsList, "Erreur", "200", null, typeDoc, new ArrayList<>(), "a", "STRICTEMENT"), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());

        //  Test avec message null
        exception = Assertions.assertThrows(MappingException.class, () -> mapper.map(new PresenceChaineCaracteresWebDto(1, 1, ruleSetsList, null, "200", "P1", typeDoc, new ArrayList<>(), "a", "STRICTEMENT"), ComplexRule.class));
        Assertions.assertEquals("Règle 1 : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple", exception.getCause().getMessage());
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
        List<String> listTypeThese = new ArrayList<>();
        listTypeThese.add("REPRO");
        complexRuleWebDto.setTypesThese(listTypeThese);
        List<Integer> ruleSetList= new ArrayList<>();
        ruleSetList.add(1);
        complexRuleWebDto.setRuleSetList(ruleSetList);
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(1,"200",null,true));
        complexRuleWebDto.addRegle(new PresenceZoneWebDto(2,"210","ET",false));

        //  Création du Mockito https://en.wikipedia.org/wiki/Mockito
        Mockito.when(ruleSetRepository.findRuleSetById(1)).thenReturn(new RuleSet(1,"libellé"));

        ComplexRule complexRule = mapper.map(complexRuleWebDto, ComplexRule.class);

        Assertions.assertEquals(complexRuleWebDto.getId(),complexRule.getId());
        Assertions.assertEquals(complexRuleWebDto.getMessage(),complexRule.getMessage());
        Assertions.assertEquals(complexRuleWebDto.getRuleSetList().get(0), new ArrayList<>(complexRule.getRuleSet()).get(0).getId());
        Assertions.assertEquals(complexRuleWebDto.getPriority(),complexRule.getPriority().toString());
        Assertions.assertEquals(complexRuleWebDto.getTypesDoc().size(),complexRule.getFamillesDocuments().size());
        Assertions.assertEquals(complexRuleWebDto.getTypesThese().size(), complexRule.getTypesThese().size());
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
        Assertions.assertEquals("Thèse", responseDto.getResultRules().get(0).getTypeDocument());
    }

}
