package fr.abes.qualimarc.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import fr.abes.qualimarc.web.dto.ControllingPpnWithRuleSetsRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PublicController.class, ObjectMapper.class})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class PublicControllerTest {

    @InjectMocks
    private PublicController publicController;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RuleService ruleService;

    @MockBean
    private NoticeBibioService noticeBibioService;

    @Test
    void getPpn() {
    }

    @Test
    void checkPpn() throws Exception {
        //  Préparation du la liste de ppn pour le Mockito
        List<String> ppnList = new ArrayList<>();
        ppnList.add("143519379");
        //  Préparation du set de règles pour le Mockito
        Set<Rule> ruleList = new HashSet<>();
        Set<FamilleDocument> familleDoc = new HashSet<>();
        familleDoc.add(new FamilleDocument("A", "Monographie"));
        ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", Priority.P1, familleDoc, true));
        //  Création de la liste de contrôle pour le Mockito
        ResultRules resultRules = new ResultRules("143519379");
        resultRules.addMessage("La zone 010 doit être présente");
        List<ResultRules> resultRulesList = new ArrayList<>();
        resultRulesList.add(resultRules);
        //  Mockito
        Mockito.when(ruleService.checkRulesOnNotices(ppnList, ruleList)).thenReturn(resultRulesList);

        //  Création de la requête
        String request = "{\"ppnList\":[\"143519379\"],\"typeAnalyse\":\"QUICK\",\"famillesDocuments\":[],\"rules\":[]}";
        //  Appel et contrôle de la méthode
        this.mockMvc.perform(post("/api/v1/check")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppn").value("143519379"));
    }
}