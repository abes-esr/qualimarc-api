package fr.abes.qualimarc.core.service;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.repository.qualimarc.ComplexRulesRepository;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import fr.abes.qualimarc.core.utils.TypeThese;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Sets;
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
import java.util.*;

@SpringBootTest(classes = {RuleService.class})
class RuleServiceTest {
    @Autowired
    RuleService service;

    @MockBean
    NoticeBibioService noticeBibioService;

    @MockBean
    ComplexRulesRepository complexRulesRepository;

    @MockBean
    ReferenceService referenceService;

    @Value("classpath:checkRules1.xml")
    Resource xmlFileNotice1;

    @Value("classpath:checkRules2.xml")
    Resource xmlFileNotice2;

    @Value("classpath:checkRules3.xml")
    Resource xmlFileNotice3;

    @Value("classpath:checkRulesDeletedPpn.xml")
    Resource xmlFileNoticeDeleted;

    @Value("classpath:theseSout.xml")
    Resource xmlTheseSout;

    @Value("classpath:theseRepro.xml")
    Resource xmlTheseRepro;

    NoticeXml notice1;
    NoticeXml notice2;
    NoticeXml notice3;
    NoticeXml noticeDeleted;
    NoticeXml theseSout;
    NoticeXml theseRepro;

    Set<ComplexRule> listeRegles;

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

        xml = IOUtils.toString(new FileInputStream(xmlFileNoticeDeleted.getFile()), StandardCharsets.UTF_8);
        noticeDeleted = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlTheseSout.getFile()), StandardCharsets.UTF_8);
        theseSout = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlTheseRepro.getFile()), StandardCharsets.UTF_8);
        theseRepro = xmlMapper.readValue(xml, NoticeXml.class);

        Set<FamilleDocument> familleDoc1 = new HashSet<>();
        familleDoc1.add(new FamilleDocument("A", "Monographie"));

        listeRegles.add(new ComplexRule(1, "La zone 010 est présente", Priority.P1, familleDoc1, new HashSet<>(), new PresenceZone(1, "010", true)));
        listeRegles.add(new ComplexRule(2, "La zone 011 est absente", Priority.P1, new PresenceZone(2, "011", false)));
        listeRegles.add(new ComplexRule(3, "La zone 012 est présente", Priority.P1, new PresenceZone(3, "012",  true)));
    }

    @Test
    void checkRulesOnNoticesAllOk() throws IOException, SQLException{
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
        Assertions.assertEquals(0, result1.getMessages().size());
        Assertions.assertEquals(1, result1.getDetailErreurs().size());
        Assertions.assertEquals("La zone 011 est absente", result1.getDetailErreurs().get(0).getMessage());
        Assertions.assertEquals("011",result1.getDetailErreurs().get(0).getZonesUnm().get(0));
        Assertions.assertEquals(1, result1.getDetailErreurs().get(0).getZonesUnm().size());
        Assertions.assertEquals(Priority.P1,result1.getDetailErreurs().get(0).getPriority());

        ResultRules result3 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("333333333")).findFirst().get();
        Assertions.assertEquals("BD", result3.getFamilleDocument().getId());
        Assertions.assertEquals(0, result3.getMessages().size());
        Assertions.assertEquals(2, result3.getDetailErreurs().size());
        Assertions.assertTrue(result3.getDetailErreurs().stream().anyMatch(r -> r.getMessage().equals("La zone 012 est présente")));
        Assertions.assertTrue(result3.getDetailErreurs().stream().anyMatch(r -> r.getZonesUnm().get(0).equals("012")));
        Assertions.assertTrue(result3.getDetailErreurs().stream().anyMatch(r -> r.getMessage().equals("La zone 011 est absente")));
        Assertions.assertTrue(result3.getDetailErreurs().stream().anyMatch(r -> r.getZonesUnm().get(0).equals("011")));
        Assertions.assertEquals(1, result3.getDetailErreurs().get(0).getZonesUnm().size());
        Assertions.assertEquals(Priority.P1,result3.getDetailErreurs().get(0).getPriority());


        Assertions.assertEquals("Titre non renseigné", result1.getTitre());
        Assertions.assertEquals("Auteur non renseigné", result1.getAuteur());
        Assertions.assertEquals("978-2-7597-0105-6", result1.getIsbn());
        Assertions.assertEquals("123456789", result1.getOcn());
        Assertions.assertEquals("30/09/2022", result1.getDateModification());
        Assertions.assertEquals("341725201", result1.getRcr());

        Assertions.assertEquals("Titre test", result3.getTitre());
        Assertions.assertEquals("Auteur test", result3.getAuteur());
        Assertions.assertEquals("07/04/2022", result3.getDateModification());
        Assertions.assertEquals("751052103", result3.getRcr());
        Assertions.assertNull(result3.getIsbn());

    }

    @Test
    void checkRulesOnNoticesUnknownPpn() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenThrow(new IllegalPpnException("le PPN 111111111 n'existe pas"));

        ResultAnalyse resultAnalyse = service.checkRulesOnNotices(ppns, listeRegles);

        Assertions.assertEquals(1, resultAnalyse.getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.getResultRules();
        Assertions.assertEquals(1, resultat.size());
        Assertions.assertEquals("le PPN 111111111 n'existe pas", resultAnalyse.getResultRules().get(0).getMessages().get(0));
    }

    @Test
    void checkRulesOnNoticesDeletedPpn() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenReturn(noticeDeleted);

        ResultAnalyse resultAnalyse = service.checkRulesOnNotices(ppns, listeRegles);

        Assertions.assertEquals(1, resultAnalyse.getPpnInconnus().size());
    }

    @Test
    void checkRulesOnNoticesSqlError() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenThrow(new SQLException("Erreur d'accès à la base de données sur PPN : 111111111"));

        ResultAnalyse resultAnalyse = service.checkRulesOnNotices(ppns, listeRegles);
        Assertions.assertEquals(1, resultAnalyse.getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.getResultRules();
        Assertions.assertEquals(1, resultat.size());

        Assertions.assertEquals("Erreur d'accès à la base de données sur PPN : 111111111", resultAnalyse.getResultRules().get(0).getMessages().get(0));

    }

    @Test
    void testIsRuleAppliedToNotice() {
        //Notice 1 est de type ressource continue
        //Notice 2 est de type Monographie
        //theseElec est de type Ressource continue et est une thèse de soutenance
        //theseRepro est de type Monographie et est une thèse de reproduction
        Set<TypeThese> setThesesRepro = new HashSet<>();
        setThesesRepro.add(TypeThese.REPRO);
        Set<TypeThese> setThesesSout = new HashSet<>();
        setThesesSout.add(TypeThese.SOUTENANCE);
        Set<FamilleDocument> setTypeResContinue = new HashSet<>();
        setTypeResContinue.add(new FamilleDocument("BD", "RessourceContinue"));
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseRepro, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesRepro, new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseRepro, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesRepro, new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice1, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesRepro, new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseSout, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesSout, new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(theseRepro, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, setThesesRepro, new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseSout, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, setThesesSout, new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice1, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, setThesesSout, new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice1, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseSout, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), new PresenceZone())));
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
        Set<ComplexRule> rules = new HashSet<>();
        rules.add(new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010", true)));

        Mockito.when(complexRulesRepository.findByPriority(Priority.P1)).thenReturn(rules);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.QUICK, null, null);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.iterator().next().getId());
    }

    /**
     * Teste le cas d'une analyse complète
     */
    @Test
    void checkRulesOnNoticesComplete() {
        List<ComplexRule> rules = new ArrayList<>();
        rules.add(new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010", true)));
        rules.add(new ComplexRule(2, "Zone 200 obligatoire", Priority.P2, new PresenceZone(2, "200", true)));

        Mockito.when(complexRulesRepository.findAll()).thenReturn(rules);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.COMPLETE, null, null);
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
        Set<ComplexRule> rules = new HashSet<>();
        rules.add(new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, typesDoc, null, new PresenceZone(1, "010", true)));

        Mockito.when(complexRulesRepository.findByFamillesDocuments(Mockito.any())).thenReturn(rules);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUSED, typesDoc, null);
        Assertions.assertIterableEquals(result, rules);
    }

    /**
     * Teste le cas d'une analyse ciblée avec jeu de règles
     */
    @Test
    void checkRulesOnNoticesFocusedRuleSet() {
        RuleSet ruleSet = new RuleSet(1, "Zones 210/214 (publication, production, diffusion)");
        Set<ComplexRule> rules = new HashSet<>();
        ComplexRule rule = new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010", true));
        rule.addRuleSet(ruleSet);
        rules.add(rule);

        Mockito.when(complexRulesRepository.findByRuleSet(ruleSet)).thenReturn(rules);

        Set<RuleSet> ruleSets = new HashSet<>();
        ruleSets.add(ruleSet);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUSED, null, ruleSets);
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

        ComplexRule rule1 = new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010", true));
        ComplexRule rule2 = new ComplexRule(2, "Zone 200 obligatoire", Priority.P1, typesDoc, null, new PresenceZone(2, "200", true));

        //déclaration du set de rule utilisé pour vérifier le résultat de l'appel à la méthode testée
        Set<ComplexRule> rulesIn = new HashSet<>();
        rulesIn.add(rule1);
        rulesIn.add(rule2);
        Set<ComplexRule> rules = new HashSet<>();
        rule1.addRuleSet(ruleSet);
        rules.add(rule1);

        Mockito.when(complexRulesRepository.findByRuleSet(ruleSet)).thenReturn(rules);


        Set<ComplexRule> rulesType = new HashSet<>();
        rulesType.add(rule2);

        Mockito.when(complexRulesRepository.findByFamillesDocuments(Mockito.any())).thenReturn(rulesType);

        Set<RuleSet> ruleSets = new HashSet<>();
        ruleSets.add(ruleSet);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUSED, typesDoc, ruleSets);

        Assertions.assertIterableEquals(Arrays.asList(result.toArray()), Arrays.asList(rulesIn.toArray()));
    }
}