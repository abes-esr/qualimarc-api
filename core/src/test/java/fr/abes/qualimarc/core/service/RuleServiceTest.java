package fr.abes.qualimarc.core.service;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.requestToCheck.RequestToCheck;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.model.entity.rules.RuleSet;
import fr.abes.qualimarc.core.model.entity.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
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

import javax.xml.transform.Result;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {RuleService.class})
class RuleServiceTest {
    @Autowired
    RuleService service;

    @MockBean
    NoticeBibioService noticeBibioService;

    @Value("classpath:111111111.xml")
    Resource xmlFileNotice1;

    @Value("classpath:222222222.xml")
    Resource xmlFileNotice2;

    @Value("classpath:333333333.xml")
    Resource xmlFileNotice3;

    NoticeXml notice1;
    NoticeXml notice2;
    NoticeXml notice3;

    List<Rule> listeRegles;

    @BeforeEach
    void initNotices() throws IOException {
        listeRegles = new ArrayList<>();

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);

        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice1.getFile()), StandardCharsets.UTF_8);
        notice1 = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlFileNotice2.getFile()), StandardCharsets.UTF_8);
        notice2 = xmlMapper.readValue(xml, NoticeXml.class);

        xml = IOUtils.toString(new FileInputStream(xmlFileNotice3.getFile()), StandardCharsets.UTF_8);
        notice3 = xmlMapper.readValue(xml, NoticeXml.class);

        List<String> typesDoc1 = new ArrayList<>();
        typesDoc1.add("A");

        listeRegles.add(new PresenceZone(1, "La zone 010 doit être présente", "010", typesDoc1, true));
        listeRegles.add(new PresenceZone(2, "La zone 011 doit être absente", "011", false));
        List<String> typesDoc2 = new ArrayList<>();
        typesDoc2.add("A");
        typesDoc2.add("BD");
        listeRegles.add(new PresenceZone(3, "La zone 012 doit être présente", "012", typesDoc2, true));
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

        List<ResultRules> resultat = service.checkRulesOnNotices(ppns, listeRegles);

        Assertions.assertEquals(3, resultat.size());

        ResultRules result1 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("111111111")).findFirst().get();
        Assertions.assertEquals(1, result1.getMessages().size());
        Assertions.assertEquals("La zone 012 doit être présente", result1.getMessages().get(0));

        ResultRules result2 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("222222222")).findFirst().get();
        Assertions.assertEquals(3, result2.getMessages().size());
        Assertions.assertEquals("La zone 010 doit être présente", result2.getMessages().get(0));
        Assertions.assertEquals("La zone 011 doit être absente", result2.getMessages().get(1));
        Assertions.assertEquals("La zone 012 doit être présente", result2.getMessages().get(2));

        ResultRules result3 = resultat.stream().filter(resultRules -> resultRules.getPpn().equals("333333333")).findFirst().get();
        Assertions.assertEquals(0, result3.getMessages().size());

    }

    @Test
    void checkRulesOnNoticesUnknownPpn() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenThrow(new IllegalPpnException("le PPN 111111111 n'existe pas"));

        List<ResultRules> resultat = service.checkRulesOnNotices(ppns, listeRegles);
        Assertions.assertEquals(1, resultat.size());
        Assertions.assertEquals("111111111", resultat.get(0).getPpn());
        Assertions.assertEquals(1, resultat.get(0).getMessages().size());
        Assertions.assertEquals("le PPN 111111111 n'existe pas", resultat.get(0).getMessages().get(0));
    }

    @Test
    void checkRulesOnNoticesSqlError() throws IOException, SQLException {
        List<String> ppns = new ArrayList<>();
        ppns.add("111111111");

        Mockito.when(noticeBibioService.getByPpn("111111111")).thenThrow(new SQLException("Erreur d'accès à la base de données sur PPN : 111111111"));

        List<ResultRules> resultat = service.checkRulesOnNotices(ppns, listeRegles);
        Assertions.assertEquals(1, resultat.size());
        Assertions.assertEquals("111111111", resultat.get(0).getPpn());
        Assertions.assertEquals(1, resultat.get(0).getMessages().size());
        Assertions.assertEquals("Erreur d'accès à la base de données sur PPN : 111111111", resultat.get(0).getMessages().get(0));
    }

//    @Test
//    void getResultQuickRulesSet() {
//        //  Prépare une liste de résultats pour contrôle
//        ResultRules resultRulesControl = new ResultRules("143519379");
//        List<ResultRules> resultRuleListControl = new ArrayList<>();
//        resultRuleListControl.add(resultRulesControl);
//
//        //  Test du jeu de règles rapide
//        List<String> ppnList = new ArrayList<>();
//        ppnList.add("143519379");
//        List<String> ruleSetList = new ArrayList<>();
//        ruleSetList.add("P1");
//        RequestToCheck request = new RequestToCheck(ppnList, ruleSetList);
//
//        List<ResultRules> resultRulesList = service.getResultRulesList(request);
//        Assertions.assertEquals(resultRuleListControl.get(0).getPpn(), resultRulesList.get(0).getPpn());
//    }
//
//    @Test
//    void getResultCompleteRuleTest() {
//        //  Prépare une liste de résultats pour contrôle
//        ResultRules resultRulesControl = new ResultRules("143519379");
//        List<ResultRules> resultRuleListControl = new ArrayList<>();
//        resultRuleListControl.add(resultRulesControl);
//
//        //  Test du jeu de règles complet
//        List<String> ppnList = new ArrayList<>();
//        ppnList.add("143519379");
//        List<String> ruleSetList = new ArrayList<>();
//        ruleSetList.add("P2");
//        RequestToCheck request = new RequestToCheck(ppnList, ruleSetList);
//
//        //  Appelle la méthode de contrôle des règles et contrôle le résultat
//        List<ResultRules> resultRulesList = service.getResultRulesList(request);
//        Assertions.assertEquals(resultRuleListControl.get(0).getPpn(), resultRulesList.get(0).getPpn());
//    }
//
//    @Test
//    void getResultFocusedRuleTest() {
//        //  Prépare une liste de résultats pour contrôle
//        ResultRules resultRulesControl = new ResultRules("143519379");
//        List<ResultRules> resultRuleListControl = new ArrayList<>();
//        resultRuleListControl.add(resultRulesControl);
//
//        //  Test du jeu de règles ciblé
//        List<String> ppnList = new ArrayList<>();
//        ppnList.add("143519379");
//        List<String> ruleSetList = new ArrayList<>();
//        ruleSetList.add("P3");
//        RequestToCheck request = new RequestToCheck(ppnList, ruleSetList);
//
//        //  Appelle la méthode de contrôle des règles et contrôle le résultat
//        List<ResultRules> resultRulesList = service.getResultRulesList(request);
//        Assertions.assertEquals(resultRuleListControl.get(0).getPpn(), resultRulesList.get(0).getPpn());
//    }

}