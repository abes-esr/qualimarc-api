package fr.abes.qualimarc.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PublicController.class, ObjectMapper.class})
@EnableWebMvc   //  Active le Model-View-Controller, nécessaire pour éviter le code d'erreur 415 lors du lancement du test checkPpn
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
        //  Création de la liste de contrôle pour le Mockito
        ResultRules resultRules = new ResultRules("143519379");
        resultRules.addMessage("La zone 010 doit être présente");
        List<ResultRules> resultRulesList = new ArrayList<>();
        resultRulesList.add(resultRules);
        //  Création du Mockito (Placer des "any" au lieu de données clairement définies afin que le Mockito fonctionne correctement (exemple : anyString() à la place de "texte")
        Mockito.when(ruleService.checkRulesOnNotices(anyList(), anySet())).thenReturn(resultRulesList);

        //  Création de l'objet ControllingPpnWithRuleSetsRequestDto à passer dans la requête
        List<String> ppnList = new ArrayList<>();
        ppnList.add("143519379");
        String typeAnalyseString = "QUICK";
        ControllingPpnWithRuleSetsRequestDto controllingPpnWithRuleSetsRequestDto = new ControllingPpnWithRuleSetsRequestDto();
        controllingPpnWithRuleSetsRequestDto.setPpnList(ppnList);
        controllingPpnWithRuleSetsRequestDto.setTypeAnalyse(typeAnalyseString);
        String jsonRequest = mapper.writeValueAsString(controllingPpnWithRuleSetsRequestDto);

        //  Appel et contrôle de la méthode
        this.mockMvc.perform(post("/api/v1/check")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ppn").value("143519379"));
    }
}