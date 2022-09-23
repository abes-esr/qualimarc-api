package fr.abes.qualimarc.core.service;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.repository.qualimarc.RulesRepository;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(classes = {RuleService.class})
class RuleServiceTest {
    @Autowired
    RuleService service;

    @MockBean
    NoticeBibioService noticeBibioService;

    @MockBean
    RulesRepository rulesRepository;

    @MockBean
    ReferenceService referenceService;

    @Value("classpath:checkRules1.xml")
    Resource xmlFileNotice1;

    @Value("classpath:checkRules2.xml")
    Resource xmlFileNotice2;

    @Value("classpath:checkRules3.xml")
    Resource xmlFileNotice3;

    @Value("classpath:theseMono.xml")
    Resource xmlTheseMono;

    NoticeXml notice1;
    NoticeXml notice2;
    NoticeXml notice3;
    NoticeXml theseMono;

    Set<Rule> listeRegles;

    @BeforeEach
    void initNotices() throws IOException {
        listeRegles = new HashSet<>();

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);

        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        notice1 = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlFileNotice2.getFile()), StandardCharsets.UTF_8);
        notice2 = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlFileNotice3.getFile()), StandardCharsets.UTF_8);
        notice3 = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlTheseMono.getFile()), StandardCharsets.UTF_8);
        theseMono = xmlMapper.readValue(xml, NoticeXml.class);

        Set<FamilleDocument> familleDoc1 = new HashSet<>();
        familleDoc1.add(new FamilleDocument("A", "Monographie"));

        listeRegles.add(new PresenceZone(1, "La zone 010 est présente", "010", Priority.P1, familleDoc1, true));
        listeRegles.add(new PresenceZone(2, "La zone 011 est absente", "011", Priority.P1, false));
        Set<FamilleDocument> typesDoc2 = new HashSet<>();
        typesDoc2.add(new FamilleDocument("A", "Monographie"));
        typesDoc2.add(new FamilleDocument("BD", "Ressource Continue"));
        listeRegles.add(new PresenceZone(3, "La zone 012 est présente", "012", Priority.P1, typesDoc2, true));
    }

    @Test
    void checkRulesOnNoticesAllOk() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");
        ppns.add("222222222");
        ppns.add("333333333");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenReturn(notice1);
        Mockito.when(noticeBibioService.getByPpn("222222222")).thenReturn(notice2);
        Mockito.when(noticeBibioService.getByPpn("333333333")).thenReturn(notice3);
        Mockito.when(referenceService.getFamilleDocument("A")).thenReturn(new FamilleDocument("A", "Monographie"));
        Mockito.when(referenceService.getFamilleDocument("O")).thenReturn(new FamilleDocument("O", "Doc Elec"));
        Mockito.when(referenceService.getFamilleDocument("BD")).thenReturn(new FamilleDocument("BD", "Ressource Continue"));

        ResultAnalyse resultAnalyse = service.checkRulesOnNotices(ppns, listeRegles);

        Assertions.assertEquals(3, resultAnalyse.getPpnAnalyses().size());
        Assertions.assertEquals(2, resultAnalyse.getPpnErrones().size());
        Assertions.assertEquals(1, resultAnalyse.getPpnOk().size());
        Assertions.assertEquals(0, resultAnalyse.getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.getResultRules();

        Assertions.assertEquals(2, resultat.size());

        ResultRules result1 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("111111111")).findFirst().get();
        Assertions.assertEquals("BD", result1.getFamilleDocument().getId());
        Assertions.assertEquals(1, result1.getMessages().size());
        Assertions.assertEquals("La zone 011 est absente", result1.getMessages().get(0));

        ResultRules result3 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("333333333")).findFirst().get();
        Assertions.assertEquals("O", result3.getFamilleDocument().getId());
        Assertions.assertEquals(1, result3.getMessages().size());
        Assertions.assertEquals("La zone 011 est absente", result3.getMessages().get(0));

    }

    @Test
    void checkRulesOnNoticesUnknownPpn() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenThrow(new IllegalPpnException("le PPN 111111111 n'existe pas"));

        ResultAnalyse resultAnalyse = service.checkRulesOnNotices(ppns, listeRegles);

        Assertions.assertEquals(1, resultAnalyse.getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.getResultRules();
        Assertions.assertEquals(0, resultat.size());
    }

    @Test
    void checkRulesOnNoticesSqlError() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenThrow(new SQLException("Erreur d'accès à la base de données sur PPN : 111111111"));

        ResultAnalyse resultAnalyse = service.checkRulesOnNotices(ppns, listeRegles);
        Assertions.assertEquals(1, resultAnalyse.getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.getResultRules();
        Assertions.assertEquals(0, resultat.size());
    }

    @Test
    void testIsRuleAppliedToNotice() {
        //cas ou la règle et la notice n'ont pas le même type de document
        Assertions.assertFalse(service.isRuleAppliedToNotice(notice1, listeRegles.stream().filter(rule -> rule.getId().equals(1)).findFirst().get()));
        //cas ou là règle et la notice ont le même type de doc "Monographie"
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice2, listeRegles.stream().filter(rule -> rule.getId().equals(1)).findFirst().get()));
        //cas ou la règle n'a pas de type de document spécifique -> s'applique à toutes les notices
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice2, listeRegles.stream().filter(rule -> rule.getId().equals(2)).findFirst().get()));

        //cas ou la règle porte sur les thèses de soutenance, et la notice aussi
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseMono, listeRegles.stream().filter(rule -> rule.getId().equals(2)).findFirst().get()));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseMono, listeRegles.stream().filter(rule -> rule.getId().equals(2)).findFirst().get()));

        Set<FamilleDocument> typesDoc1 = new HashSet<>();
        typesDoc1.add(new FamilleDocument("TS", "Thèse de soutenance"));

        Rule rule = new PresenceZone(1, "La zone 010 est présente", "010", Priority.P1, typesDoc1, true);
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseMono, rule));

    }


    /**
     * teste le cas d'un type d'analyse inconnu
     */
    @Test
    void checkRulesOnNoticesNoAnalyse() {
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.UNKNOWN, null, null));
    }

    /**
     * Teste le cas d'une analyse ciblée sans paramètres ou paramètres vides
     */
    @Test
    void checkRulesOnNoticesFocusedNoParams() {
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUSED, null, null));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUSED, new HashSet<>(), null));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUSED, null, new HashSet<>()));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUSED, new HashSet<>(), new HashSet<>()));
    }

    /**
     * teste le cas d'une analyse rapide
     */
    @Test
    void checkRulesOnNoticesQuick() {
        Set<Rule> rules = new HashSet<>();
        rules.add(new PresenceZone(1, "Zone 010 obligatoire", "010", Priority.P1, true));

        Mockito.when(rulesRepository.findByPriority(Priority.P1)).thenReturn(rules);

        Set<Rule> result = service.getResultRulesList(TypeAnalyse.QUICK, null, null);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.iterator().next().getId());
    }

    /**
     * Teste le cas d'une analyse complète
     */
    @Test
    void checkRulesOnNoticesComplete() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new PresenceZone(1, "Zone 010 obligatoire", "010", Priority.P1, true));
        rules.add(new PresenceZone(2, "Zone 200 obligatoire", "200", Priority.P2, true));

        Mockito.when(rulesRepository.findAll()).thenReturn(rules);

        Set<Rule> result = service.getResultRulesList(TypeAnalyse.COMPLETE, null, null);
        //les listes contenant plusieurs objets, assertIterableIquals peut renvoyer false s'ils ne sont pas dans le même ordre, on compare donc les listes éléments par éléments
        Assertions.assertTrue(result.size() == rules.size() && result.containsAll(rules) && rules.containsAll(result));
    }

    /**
     * Teste le cas d'une analyse ciblée avec types de documents
     */
    @Test
    void checkRulesOnNoticesFocusedTypeDoc() {
        Set<FamilleDocument> typesDoc = new HashSet<>();
        typesDoc.add(new FamilleDocument("B", "Audiovisuel"));
        Set<Rule> rules = new HashSet<>();
        rules.add(new PresenceZone(1, "Zone 010 obligatoire", "010", Priority.P1, typesDoc, true));

        Mockito.when(rulesRepository.findByFamillesDocuments(Mockito.any())).thenReturn(rules);

        Set<Rule> result = service.getResultRulesList(TypeAnalyse.FOCUSED, typesDoc, null);
        Assertions.assertIterableEquals(result, rules);
    }

    /**
     * Teste le cas d'une analyse ciblée avec jeu de règles
     */
    @Test
    void checkRulesOnNoticesFocusedRuleSet() {
        RuleSet ruleSet = new RuleSet(1, "Zones 210/214 (publication, production, diffusion)");
        Set<Rule> rules = new HashSet<>();
        Rule rule = new PresenceZone(1, "Zone 010 obligatoire", "010", Priority.P1, null, true);
        rule.addRuleSet(ruleSet);
        rules.add(rule);

        Mockito.when(rulesRepository.findByRuleSet(ruleSet)).thenReturn(rules);

        Set<RuleSet> ruleSets = new HashSet<>();
        ruleSets.add(ruleSet);

        Set<Rule> result = service.getResultRulesList(TypeAnalyse.FOCUSED, null, ruleSets);
        //les listes ne contenant qu'un élément on utilise assertIterableEquals pour vérifier qu'elles sont identiques
        Assertions.assertIterableEquals(result, rules);
    }

    /**
     * Teste le cas d'une analyse ciblée avec jeu de règles et types de documents
     */
    @Test
    void checkRulesOnNoticesFocusedTypeDocRuleSet() {
        RuleSet ruleSet = new RuleSet(1, "Zones 210/214 (publication, production, diffusion)");

        Set<FamilleDocument> typesDoc = new HashSet<>();
        typesDoc.add(new FamilleDocument("B", "Audiovisuel"));

        Rule rule1 = new PresenceZone(1, "Zone 010 obligatoire", "010", Priority.P1, null, true);
        Rule rule2 = (new PresenceZone(2, "Zone 200 obligatoire", "200", Priority.P1, typesDoc, true));

        //déclaration du set de rule utilisé pour vérifier le résultat de l'appel à la méthode testée
        Set<Rule> rulesIn = new HashSet<>();
        rulesIn.add(rule1);
        rulesIn.add(rule2);
        Set<Rule> rules = new HashSet<>();
        rule1.addRuleSet(ruleSet);
        rules.add(rule1);

        Mockito.when(rulesRepository.findByRuleSet(ruleSet)).thenReturn(rules);


        Set<Rule> rulesType = new HashSet<>();
        rulesType.add(rule2);

        Mockito.when(rulesRepository.findByFamillesDocuments(Mockito.any())).thenReturn(rulesType);

        Set<RuleSet> ruleSets = new HashSet<>();
        ruleSets.add(ruleSet);

        Set<Rule> result = service.getResultRulesList(TypeAnalyse.FOCUSED, typesDoc, ruleSets);

        Assertions.assertIterableEquals(result, rulesIn);
    }
}