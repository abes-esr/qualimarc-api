package fr.abes.qualimarc.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.configuration.WebConfig;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {RuleController.class, ObjectMapper.class}) //  Active le Model-View-Controller, nécessaire pour éviter le code d'erreur 415 lors du lancement du test checkPpn
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WebConfig.class})
public class RuleControllerTest {

    @InjectMocks
    private RuleController ruleController;

    @Autowired
    private MappingJackson2HttpMessageConverter yamlHttpConverter;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonHttpConverter;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RuleService ruleService;

    @MockBean
    private NoticeBibioService noticeBibioService;

    @MockBean
    private UtilsMapper utilsMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.ruleController)
                .setMessageConverters(this.yamlHttpConverter, this.jsonHttpConverter).
                        build();
    }

    @Test
    void checkPpn() throws Exception {
        //  Création de la liste de contrôle pour le Mockito
        ResultRulesResponseDto resultRulesResponseDto = new ResultRulesResponseDto();
        resultRulesResponseDto.setPpn("143519379");
        List<ResultRulesResponseDto> resultRulesResponseDtoList = new ArrayList<>();
        resultRulesResponseDtoList.add(resultRulesResponseDto);
        ResultAnalyseResponseDto resultAnalyseResponseDto = new ResultAnalyseResponseDto();
        resultAnalyseResponseDto.setResultRules(resultRulesResponseDtoList);
        //  Création du Mockito
        Mockito.when(utilsMapper.map(any(),any())).thenReturn(resultAnalyseResponseDto);

        //  Création de l'objet ControllingPpnWithRuleSetsRequestDto à passer dans la requête
        List<String> ppnList = new ArrayList<>();
        ppnList.add("143519379");
        TypeAnalyse typeAnalyse;
        typeAnalyse = TypeAnalyse.QUICK;
        PpnWithRuleSetsRequestDto ppnWithRuleSetsRequestDto = new PpnWithRuleSetsRequestDto();
        ppnWithRuleSetsRequestDto.setPpnList(ppnList);
        ppnWithRuleSetsRequestDto.setTypeAnalyse(typeAnalyse);
        String jsonRequest = objectMapper.writeValueAsString(ppnWithRuleSetsRequestDto);

        //  Appel et contrôle de la méthode
        this.mockMvc.perform(post("/api/v1/check")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultRules[0].ppn").value("143519379"));
    }

    @Test
    void testIndexRules() throws Exception {
        String yaml =
                "rules:\n" +
                "    - id:          2\n" +
                "      id-excel:    2\n" +
                "      type:        presencezone\n" +
                "      message:     message test 2\n" +
                "      zone:        330\n" +
                "      priorite:    P2\n" +
                "      presence:    false\n";

        this.mockMvc.perform(post("/api/v1/indexRules")
                .contentType("text/yml").characterEncoding(StandardCharsets.UTF_8)
                .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }
}