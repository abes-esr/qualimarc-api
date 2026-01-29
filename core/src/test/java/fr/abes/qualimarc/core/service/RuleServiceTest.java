package fr.abes.qualimarc.core.service;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.configuration.AsyncConfiguration;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.exception.IllegalRulesSetException;
import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.DependencyRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.LinkedRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.repository.qualimarc.ComplexRulesRepository;
import fr.abes.qualimarc.core.utils.*;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = {RuleService.class, AsyncConfiguration.class})
public class RuleServiceTest {
    @Autowired
    RuleService service;

    @MockitoBean
    NoticeService noticeService;

    @MockitoBean
    ComplexRulesRepository complexRulesRepository;

    @MockitoBean
    ReferenceService referenceService;

    @MockitoBean
    JournalService journalService;

    @Value("classpath:143519379.xml")
    Resource xmlFileNoticeBiblio;

    @Value("classpath:02787088X.xml")
    Resource xmlFileNoticeAutorite1;

    @Value("classpath:02731667X.xml")
    Resource xmlFileNoticeAutorite2;

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

    @Value("classpath:noticeSansFamille.xml")
    Resource xmlNoticeSansFamille;

    @Autowired
    AsyncConfiguration asyncExecutor;

    NoticeXml notice1;
    NoticeXml notice2;
    NoticeXml notice3;
    NoticeXml noticeDeleted;
    NoticeXml theseSout;
    NoticeXml theseRepro;
    NoticeXml noticeBiblio;
    NoticeXml noticeAutorite1;
    NoticeXml noticeAutorite2;
    NoticeXml noticeSansFamille;
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

        xml = IOUtils.toString(new FileInputStream(xmlFileNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        noticeBiblio = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlFileNoticeAutorite1.getFile()), StandardCharsets.UTF_8);
        noticeAutorite1 = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlFileNoticeAutorite2.getFile()), StandardCharsets.UTF_8);
        noticeAutorite2 = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlNoticeSansFamille.getFile()), StandardCharsets.UTF_8);
        noticeSansFamille = xmlMapper.readValue(xml, NoticeXml.class);

        Set<FamilleDocument> familleDoc1 = new HashSet<>();
        familleDoc1.add(new FamilleDocument("A", "Monographie"));

        Set<TypeThese> typesThese = new HashSet<>();
        typesThese.add(TypeThese.REPRO);
        Set<RuleSet> ruleSet = new HashSet<>();

        listeRegles.add(new ComplexRule(1, "La zone 010 est présente", Priority.P1, familleDoc1, typesThese, ruleSet, new PresenceZone(1, "010",true, true)));
        listeRegles.add(new ComplexRule(2, "La zone 011 est absente", Priority.P1, new PresenceZone(2, "011",true, false)));
        listeRegles.add(new ComplexRule(3, "La zone 012 est présente", Priority.P1, new PresenceZone(3, "012",true,  true)));
    }

    @Test
    void checkRulesOnNoticesAllOk() throws IOException, SQLException, ExecutionException, InterruptedException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");
        ppns.add("222222222");
        ppns.add("333333333");

        Mockito.when(noticeService.getBiblioByPpn("111111111")).thenReturn(notice1);
        Mockito.when(noticeService.getBiblioByPpn("222222222")).thenReturn(notice2);
        Mockito.when(noticeService.getBiblioByPpn("333333333")).thenReturn(notice3);
        Mockito.when(referenceService.getFamilleDocument("A")).thenReturn(new FamilleDocument("A", "Monographie"));
        Mockito.when(referenceService.getFamilleDocument("O")).thenReturn(new FamilleDocument("O", "Doc Elec"));
        Mockito.when(referenceService.getFamilleDocument("BD")).thenReturn(new FamilleDocument("BD", "Ressource Continue"));

        CompletableFuture<ResultAnalyse> resultAnalyse = service.checkRulesOnNotices(1,listeRegles, ppns, false);

        Assertions.assertEquals(3, resultAnalyse.get().getPpnAnalyses().size());
        Assertions.assertEquals(2, resultAnalyse.get().getPpnErrones().size());
        Assertions.assertEquals(1, resultAnalyse.get().getPpnOk().size());
        Assertions.assertEquals(0, resultAnalyse.get().getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.get().getResultRules();

        Assertions.assertEquals(2, resultat.size());


        ResultRules result1 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("111111111")).findFirst().orElse(null);
        Assertions.assertNotNull(result1);
        Assertions.assertEquals("BD", result1.getFamilleDocument().getId());
        Assertions.assertEquals(0, result1.getMessages().size());
        Assertions.assertEquals(1, result1.getDetailErreurs().size());
        result1.getDetailErreurs().sort(Comparator.comparing(o -> o.getZonesUnm().get(0)));
        Assertions.assertEquals("La zone 011 est absente", result1.getDetailErreurs().get(0).getMessage());
        Assertions.assertEquals("011",result1.getDetailErreurs().get(0).getZonesUnm().get(0));
        Assertions.assertEquals(1, result1.getDetailErreurs().get(0).getZonesUnm().size());
        Assertions.assertEquals(Priority.P1,result1.getDetailErreurs().get(0).getPriority());

        ResultRules result3 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("333333333")).findFirst().orElse(null);
        Assertions.assertNotNull(result3);
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
    void checkRulesOnNoticesUnknownPpn() throws IOException, SQLException, ExecutionException, InterruptedException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeService.getBiblioByPpn("111111111")).thenThrow(new IllegalPpnException("le PPN 111111111 n'existe pas"));

        CompletableFuture<ResultAnalyse> resultAnalyse = service.checkRulesOnNotices(1,listeRegles, ppns, false);

        Assertions.assertEquals(1, resultAnalyse.get().getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.get().getResultRules();
        Assertions.assertEquals(1, resultat.size());
        Assertions.assertEquals("le PPN 111111111 n'existe pas", resultAnalyse.get().getResultRules().get(0).getMessages().get(0));
    }

    @Test
    void checkRulesOnNoticesDeletedPpn() throws IOException, SQLException, ExecutionException, InterruptedException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeService.getBiblioByPpn("111111111")).thenReturn(noticeDeleted);

        CompletableFuture<ResultAnalyse> resultAnalyse = service.checkRulesOnNotices(1,listeRegles, ppns, false);

        Assertions.assertEquals(1, resultAnalyse.get().getPpnInconnus().size());
    }

    @Test
    void checkRulesOnNoticesSqlError() throws IOException, SQLException, ExecutionException, InterruptedException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeService.getBiblioByPpn("111111111")).thenThrow(new SQLException("Erreur d'accès à la base de données sur PPN : 111111111"));

        CompletableFuture<ResultAnalyse> resultAnalyse = service.checkRulesOnNotices(1,listeRegles, ppns, false);
        Assertions.assertEquals(1, resultAnalyse.get().getPpnInconnus().size());

        List<ResultRules> resultat = resultAnalyse.get().getResultRules();
        Assertions.assertEquals(1, resultat.size());

        Assertions.assertEquals("Erreur d'accès à la base de données sur PPN : 111111111", resultAnalyse.get().getResultRules().get(0).getMessages().get(0));

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
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseRepro, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesRepro, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseRepro, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesRepro, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(notice1, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesRepro, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseSout, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), setThesesSout, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(theseRepro, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
        Assertions.assertFalse(service.isRuleAppliedToNotice(notice2, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, setThesesRepro, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseSout, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, setThesesSout, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice1, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, setThesesSout, Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(notice1, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
        Assertions.assertTrue(service.isRuleAppliedToNotice(theseSout, new ComplexRule(1, "test", Priority.P1, setTypeResContinue, Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));

        Assertions.assertThrows(IllegalTypeDocumentException.class, () -> service.isRuleAppliedToNotice(noticeSansFamille, new ComplexRule(1, "test", Priority.P1, Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet(), new PresenceZone())));
    }


    /**
     * teste le cas d'un type d'analyse inconnu
     */
    @Test
    void checkRulesOnNoticesNoAnalyse() {
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.UNKNOWN, null, null, null));
    }

    /**
     * Teste le cas d'une analyse ciblée sans paramètres ou paramètres vides
     */
    @Test
    void checkRulesOnNoticesFocusedNoParams() {
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, null, null, null));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, new HashSet<>(), null, null));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, null, null, new HashSet<>()));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, new HashSet<>(), null, new HashSet<>()));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, null, new HashSet<>(), null));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, new HashSet<>(), new HashSet<>(), null));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, null, new HashSet<>(), new HashSet<>()));
        Assertions.assertThrows(IllegalRulesSetException.class, () -> service.getResultRulesList(TypeAnalyse.FOCUS, new HashSet<>(), new HashSet<>(), new HashSet<>()));
    }

    /**
     * teste le cas d'une analyse rapide
     */
    @Test
    void checkRulesOnNoticesQuick() {
        Set<ComplexRule> rules = new HashSet<>();
        rules.add(new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010",false, true)));

        Mockito.when(complexRulesRepository.findByPriority(Priority.P1)).thenReturn(rules);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.QUICK, null, null, null);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.iterator().next().getId());
    }

    /**
     * Teste le cas d'une analyse complète
     */
    @Test
    void checkRulesOnNoticesComplete() {
        List<ComplexRule> rules = new ArrayList<>();
        rules.add(new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010",false, true)));
        rules.add(new ComplexRule(2, "Zone 200 obligatoire", Priority.P2, new PresenceZone(2, "200",false, true)));

        Mockito.when(complexRulesRepository.findAll()).thenReturn(rules);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.COMPLETE, null, null, null);
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
        Set<ComplexRule> rulesIn = new HashSet<>();
        rulesIn.add(new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, typesDoc, Sets.newHashSet(), null, new PresenceZone(1, "010",false, true)));

        Mockito.when(complexRulesRepository.findByFamillesDocuments(Mockito.any())).thenReturn(rulesIn);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUS, typesDoc, null,null);
        Assertions.assertTrue(result.size() == rulesIn.size() && result.containsAll(rulesIn) && rulesIn.containsAll(result));
    }

    /**
     * Teste le cas d'une analyse sur notice avec famille de document inconnue
     */
    @Test
    void checkRulesOnNoticesWithUnknownFamilleDoc() throws IOException, SQLException, ExecutionException, InterruptedException {
        Set<FamilleDocument> typeDoc = new HashSet<>();
        typeDoc.add(new FamilleDocument("A", "Monographie"));
        Set<ComplexRule> rules = new HashSet<>();
        rules.add(new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, typeDoc, Sets.newHashSet(), null, new PresenceZone(1, "010",false, true)));

        Mockito.when(noticeService.getBiblioByPpn("123456789")).thenReturn(noticeAutorite1);

        CompletableFuture<ResultAnalyse> result = service.checkRulesOnNotices(1,rules, Lists.newArrayList("123456789"), false);
        Assertions.assertEquals("123456789", result.get().getPpnInconnus().iterator().next());
        Assertions.assertEquals(0, result.get().getPpnOk().size());
        Assertions.assertEquals(0, result.get().getPpnErrones().size());
        Assertions.assertEquals(1, result.get().getPpnAnalyses().size());
    }

    /**
     * Teste le cas d'une analyse ciblée avec jeu de règles
     */
    @Test
    void checkRulesOnNoticesFocusedRuleSet() {
        RuleSet ruleSet = new RuleSet(1, "Zones 210/214 (publication, production, diffusion)");
        Set<ComplexRule> rulesIn = new HashSet<>();
        ComplexRule rule = new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010",false, true));
        rule.addRuleSet(ruleSet);
        rulesIn.add(rule);

        Mockito.when(complexRulesRepository.findByRuleSet(ruleSet)).thenReturn(rulesIn);

        Set<RuleSet> ruleSets = new HashSet<>();
        ruleSets.add(ruleSet);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUS, null, null, ruleSets);
        //les listes ne contenant qu'un élément on utilise assertIterableEquals pour vérifier qu'elles sont identiques
        Assertions.assertTrue(result.size() == rulesIn.size() && result.containsAll(rulesIn) && rulesIn.containsAll(result));
    }

    /**
     * Teste le cas d'une analyse ciblée avec type de thèse
     */
    @Test
    void checkRulesOnNoticesFocusedTypeThese() {
        Set<ComplexRule> rulesIn = new HashSet<>();
        ComplexRule rule = new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010",false, true));
        rule.addTypeThese(TypeThese.REPRO);
        rulesIn.add(rule);

        Mockito.when(complexRulesRepository.findByTypesThese(TypeThese.REPRO)).thenReturn(rulesIn);

        Set<TypeThese> typeTheseSet = new HashSet<>();
        typeTheseSet.add(TypeThese.REPRO);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUS, null, typeTheseSet, null);
        //les listes ne contenant qu'un élément on utilise assertIterableEquals pour vérifier qu'elles sont identiques
        Assertions.assertTrue(result.size() == rulesIn.size() && result.containsAll(rulesIn) && rulesIn.containsAll(result));
    }

    /**
     * Teste le cas d'une analyse ciblée avec jeu de règles et types de documents
     */
    @Test
    void checkRulesOnNoticesFocusedTypeDocRuleSet() {
        RuleSet ruleSet = new RuleSet(1, "Zones 210/214 (publication, production, diffusion)");

        Set<FamilleDocument> typesDoc = new HashSet<>();
        typesDoc.add(new FamilleDocument("B", "Audiovisuel"));

        ComplexRule rule1 = new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010",false, true));
        ComplexRule rule2 = new ComplexRule(2, "Zone 200 obligatoire", Priority.P1, typesDoc, Sets.newHashSet(), null, new PresenceZone(2, "200",false, true));

        //déclaration du set de rule utilisé pour vérifier le résultat de l'appel à la méthode testée
        Set<ComplexRule> rulesIn = new HashSet<>();
        rulesIn.add(rule2);

        //jeu de règle retourné par la récupération des règles par jeu de règle personnalisé
        Set<ComplexRule> rulesRuleSet = new HashSet<>();
        rulesRuleSet.add(rule1);
        rulesRuleSet.add(rule2);

        Mockito.when(complexRulesRepository.findByRuleSet(ruleSet)).thenReturn(rulesRuleSet);

        //jeu de règle retourné par la récupération des règles par familles de documents
        Set<ComplexRule> rulesType = new HashSet<>();
        rulesType.add(rule2);

        Mockito.when(complexRulesRepository.findByFamillesDocuments(Mockito.any())).thenReturn(rulesType);

        Set<RuleSet> ruleSets = new HashSet<>();
        ruleSets.add(ruleSet);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUS, typesDoc, null, ruleSets);

        Assertions.assertTrue(result.size() == rulesIn.size() && result.containsAll(rulesIn) && rulesIn.containsAll(result));
    }

    /**
     * Teste le cas d'une analyse ciblée avec jeu de règles et familles de documents de type thèse
     */
    @Test
    void checkRulesOnNoticesFocusedTypeTheseRuleSet() {
        RuleSet ruleSet = new RuleSet(1, "Zones 210/214 (publication, production, diffusion)");

        ComplexRule rule1 = new ComplexRule(1, "Zone 010 obligatoire", Priority.P1, new PresenceZone(1, "010",false, true));
        ComplexRule rule2 = new ComplexRule(2, "Zone 200 obligatoire", Priority.P1, new PresenceZone(2, "200",false, true));

        //déclaration du set de rule utilisé pour vérifier le résultat de l'appel à la méthode testée
        Set<ComplexRule> rulesIn = new HashSet<>();
        rulesIn.add(rule2);

        //jeu de règle retourné par la récupération des règles par jeu de règle personnalisé
        Set<ComplexRule> rulesRuleSet = new HashSet<>();
        rulesRuleSet.add(rule1);
        rulesRuleSet.add(rule2);

        Mockito.when(complexRulesRepository.findByRuleSet(ruleSet)).thenReturn(rulesRuleSet);

        //jeu de règle retourné par la récupération des règles par familles de documents
        Set<ComplexRule> rulesTypeThese = new HashSet<>();
        rulesTypeThese.add(rule2);

        Mockito.when(complexRulesRepository.findByTypesThese(Mockito.any())).thenReturn(rulesTypeThese);

        Set<RuleSet> ruleSets = new HashSet<>();
        ruleSets.add(ruleSet);

        Set<TypeThese> typeThese = new HashSet<>();
        typeThese.add(TypeThese.REPRO);

        Set<ComplexRule> result = service.getResultRulesList(TypeAnalyse.FOCUS, null, typeThese, ruleSets);

        Assertions.assertTrue(result.size() == rulesIn.size() && result.containsAll(rulesIn) && rulesIn.containsAll(result));
    }

    @Test
    void checkRulesOnNoticesDependency() throws SQLException, IOException, ExecutionException, InterruptedException {
        List<String> ppns = new ArrayList<>();
        ppns.add("143519379");

        ComplexRule rule = new ComplexRule(1, "Message", Priority.P1, new PresenceZone(1, "200",false, true));
        rule.addOtherRule(new DependencyRule(1, "607", "3", TypeNoticeLiee.AUTORITE,0, 1, rule));
        rule.addOtherRule(new LinkedRule(new PresenceZone(2, "152",false, true), BooleanOperateur.ET, 2, rule));
        Set<ComplexRule> listeReglesDependency = new HashSet<>();
        listeReglesDependency.add(rule);

        Mockito.when(noticeService.getBiblioByPpn("143519379")).thenReturn(noticeBiblio);
        Mockito.when(noticeService.getAutoriteByPpn("02787088X")).thenReturn(noticeAutorite1);
        Mockito.when(noticeService.getAutoriteByPpn("02731667X")).thenReturn(noticeAutorite2);
        Mockito.when(noticeService.getAutoriteByPpn("987654321")).thenReturn(null);

        Mockito.when(referenceService.getFamilleDocument("A")).thenReturn(new FamilleDocument("A", "Monographie"));

        CompletableFuture<ResultAnalyse> resultAnalyse = service.checkRulesOnNotices(1,listeReglesDependency, ppns, false);
        Assertions.assertEquals(1, resultAnalyse.get().getPpnAnalyses().size());
        Assertions.assertEquals(1, resultAnalyse.get().getPpnErrones().size());
        Assertions.assertEquals(0, resultAnalyse.get().getPpnOk().size());
        Assertions.assertEquals(0, resultAnalyse.get().getPpnInconnus().size());
        Assertions.assertEquals(2, resultAnalyse.get().getResultRules().get(0).getDetailErreurs().size());
        Assertions.assertTrue(resultAnalyse.get().getResultRules().get(0).getDetailErreurs().stream().anyMatch(el -> el.getMessage().equals(rule.getMessage() + " PPN lié : " + "02787088X")));
        Assertions.assertTrue(resultAnalyse.get().getResultRules().get(0).getDetailErreurs().stream().anyMatch(el -> el.getMessage().equals(rule.getMessage() + " PPN lié : " + "02731667X")));
    }
 }
