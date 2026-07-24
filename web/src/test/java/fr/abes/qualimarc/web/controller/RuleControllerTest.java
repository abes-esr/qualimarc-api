package fr.abes.qualimarc.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.configuration.AsyncConfiguration;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.service.JournalService;
import fr.abes.qualimarc.core.service.NoticeService;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.service.RuleService;
import fr.abes.qualimarc.core.utils.TypeAnalyse;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.configuration.WebConfig;
import fr.abes.qualimarc.web.dto.PpnWithRuleSetsRequestDto;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.exception.ExceptionControllerHandler;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {RuleController.class})
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {WebConfig.class, AsyncConfiguration.class, ExceptionControllerHandler.class})
public class RuleControllerTest {
    @Autowired
    WebApplicationContext context;

    @Autowired
    MappingJackson2HttpMessageConverter yamlHttpConverter;

    @Autowired
    MappingJackson2HttpMessageConverter jsonHttpConverter;

    MockMvc mockMvc;

    @Autowired
    AsyncConfiguration asyncExecutor;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RuleService ruleService;

    @MockitoBean
    NoticeService noticeService;

    @MockitoBean
    ReferenceService referenceService;

    @MockitoBean
    JournalService journalService;

    @MockitoBean
    UtilsMapper utilsMapper;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void checkPpnWithOneThread() throws Exception {
        ResultRulesResponseDto resultRulesResponseDto = new ResultRulesResponseDto();
        resultRulesResponseDto.setPpn("143519379");
        List<ResultRulesResponseDto> resultRulesResponseDtoList = new ArrayList<>();
        resultRulesResponseDtoList.add(resultRulesResponseDto);
        ResultAnalyseResponseDto resultAnalyseResponseDto = new ResultAnalyseResponseDto();
        resultAnalyseResponseDto.setResultRules(resultRulesResponseDtoList);

        Mockito.doNothing().when(journalService).saveJournalAnalyse(Mockito.any());
        Mockito.when(utilsMapper.map(any(), any())).thenReturn(resultAnalyseResponseDto);
        ResultAnalyse resultAnalyse = new ResultAnalyse();
        resultAnalyse.setPpnAnalyses(Sets.newLinkedHashSet("143519379"));
        Mockito.when(ruleService.checkRulesOnNotices(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(CompletableFuture.completedFuture(resultAnalyse));

        PpnWithRuleSetsRequestDto requestDto = new PpnWithRuleSetsRequestDto();
        requestDto.setId(123);
        requestDto.setPpnList(List.of("143519379"));
        requestDto.setTypeAnalyse(TypeAnalyse.QUICK);

        this.mockMvc.perform(post("/api/v1/check")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value("123"));

        waitForCompletedResult(123)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultRules[0].ppn").value("143519379"));
    }

    @Test
    void checkPpnWitMultiThread() throws Exception {
        List<ResultRulesResponseDto> resultRulesResponseDtoList = new ArrayList<>();
        resultRulesResponseDtoList.add(new ResultRulesResponseDto("143519379", new ArrayList<>()));
        resultRulesResponseDtoList.add(new ResultRulesResponseDto("123456789", new ArrayList<>()));
        resultRulesResponseDtoList.add(new ResultRulesResponseDto("987654321", new ArrayList<>()));
        resultRulesResponseDtoList.add(new ResultRulesResponseDto("654987321", new ArrayList<>()));
        ResultAnalyseResponseDto resultAnalyseResponseDto = new ResultAnalyseResponseDto();
        resultAnalyseResponseDto.setResultRules(resultRulesResponseDtoList);

        Mockito.doNothing().when(journalService).saveJournalAnalyse(Mockito.any());
        Mockito.when(utilsMapper.map(any(), any())).thenReturn(resultAnalyseResponseDto);
        ResultAnalyse resultAnalyse = new ResultAnalyse();
        resultAnalyse.setPpnAnalyses(Sets.newLinkedHashSet("143519379", "123456789", "987654321", "654987321"));
        Mockito.when(ruleService.checkRulesOnNotices(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(CompletableFuture.completedFuture(resultAnalyse));

        PpnWithRuleSetsRequestDto requestDto = new PpnWithRuleSetsRequestDto();
        requestDto.setId(456);
        requestDto.setPpnList(List.of("143519379", "123456789", "987654321", "654987321"));
        requestDto.setTypeAnalyse(TypeAnalyse.QUICK);

        this.mockMvc.perform(post("/api/v1/check")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value("456"));

        waitForCompletedResult(456)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultRules[0].ppn").value("143519379"))
                .andExpect(jsonPath("$.resultRules[1].ppn").value("123456789"))
                .andExpect(jsonPath("$.resultRules[2].ppn").value("987654321"))
                .andExpect(jsonPath("$.resultRules[3].ppn").value("654987321"));
    }

    @Test
    void checkPpnWithMultiThreadLoadsRulesOnlyOnce() throws Exception {
        Mockito.doNothing().when(journalService).saveJournalAnalyse(Mockito.any());
        Mockito.when(utilsMapper.map(any(), any())).thenReturn(new ResultAnalyseResponseDto());
        Mockito.when(ruleService.getResultRulesList(Mockito.eq(TypeAnalyse.QUICK), Mockito.anySet(), Mockito.anySet(), Mockito.anySet()))
                .thenReturn(Collections.emptySet());

        ResultAnalyse resultAnalyse = new ResultAnalyse();
        resultAnalyse.setPpnAnalyses(Sets.newLinkedHashSet("143519379", "123456789", "987654321", "654987321"));
        Mockito.when(ruleService.checkRulesOnNotices(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(
                        CompletableFuture.completedFuture(resultAnalyse),
                        CompletableFuture.completedFuture(resultAnalyse)
                );

        PpnWithRuleSetsRequestDto request = new PpnWithRuleSetsRequestDto();
        request.setId(42);
        request.setPpnList(List.of("143519379", "123456789", "987654321", "654987321"));
        request.setTypeAnalyse(TypeAnalyse.QUICK);

        this.mockMvc.perform(post("/api/v1/check")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value("42"));

        waitForCompletedResult(42)
                .andExpect(status().isOk());

        Mockito.verify(ruleService, Mockito.times(1))
                .getResultRulesList(Mockito.eq(TypeAnalyse.QUICK), Mockito.anySet(), Mockito.anySet(), Mockito.anySet());
    }

    @Test
    void getResultReturnsAcceptedWhileAnalysisStillRunning() throws Exception {
        ResultAnalyse resultAnalyse = new ResultAnalyse();
        ResultAnalyseResponseDto resultAnalyseResponseDto = new ResultAnalyseResponseDto();

        Mockito.doNothing().when(journalService).saveJournalAnalyse(Mockito.any());
        Mockito.when(utilsMapper.map(any(), any())).thenReturn(resultAnalyseResponseDto);
        Mockito.when(ruleService.checkRulesOnNotices(Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(CompletableFuture.supplyAsync(
                        () -> resultAnalyse,
                        CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS)
                ));

        PpnWithRuleSetsRequestDto requestDto = new PpnWithRuleSetsRequestDto();
        requestDto.setId(789);
        requestDto.setPpnList(List.of("143519379"));
        requestDto.setTypeAnalyse(TypeAnalyse.QUICK);

        this.mockMvc.perform(post("/api/v1/check")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value("789"));

        this.mockMvc.perform(get("/api/v1/result/789")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isAccepted());

        waitForCompletedResult(789)
                .andExpect(status().isOk());
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
                .contentType("application/x-yaml").characterEncoding(StandardCharsets.UTF_8)
                .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test création règle simple de type dependance")
    void testIndexRuleDependency() throws Exception {
        String yaml =
                "rules:\n" +
                        "    - id:          2\n" +
                        "      id-excel:    2\n" +
                        "      type:        dependance\n" +
                        "      zone:        330\n" +
                        "      souszone:    a";

        this.mockMvc.perform(post("/api/v1/indexRules")
                .contentType("application/x-yaml").characterEncoding(StandardCharsets.UTF_8)
                .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getContentAsString().contains("debugMessage: Une règle simple ne peut pas être une règle de dépendance"));
    }

    @Test
    @DisplayName("test création règle simple de type reciprocite")
    void testIndexRuleReciprocite() throws Exception {
        String yaml =
                "rules:\n" +
                        "    - id:          2\n" +
                        "      id-excel:    2\n" +
                        "      type:        reciprocite\n" +
                        "      zone:        330\n" +
                        "      souszone:    a";

        this.mockMvc.perform(post("/api/v1/indexRules")
                .contentType("application/x-yaml").characterEncoding(StandardCharsets.UTF_8)
                .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getContentAsString().contains("debugMessage: Une règle simple ne peut pas être de type reciprocite"));
    }

    @Test
    void testIndexCompleRule() throws Exception {
        String yaml =
                "---\n" +
                "rules:\n" +
                "   - id: 2\n" +
                "     id-excel: 2\n" +
                "     message: test\n" +
                "     priorite: P2\n" +
                "     type-doc:\n" +
                "       - A\n" +
                "       - O\n" +
                "     regles:\n" +
                "       - id: 2\n" +
                "         type: presencezone\n" +
                "         zone: '330'\n" +
                "         presence: false\n" +
                "       - id: 3\n" +
                "         type: presencezone\n" +
                "         zone: '330'\n" +
                "         presence: true\n" +
                "         operateur-booleen: ET\n";

        this.mockMvc.perform(post("/api/v1/indexComplexRules")
                .contentType("application/x-yaml").characterEncoding(StandardCharsets.UTF_8)
                .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test creation regle complexe avec dependance et position negative")
    void testIndexComplexRuleDependencyWithNegativePosition() throws Exception {
        String yaml =
                "---\n" +
                "rules:\n" +
                "   - id: 2\n" +
                "     id-excel: 2\n" +
                "     message: test dependance -1\n" +
                "     priorite: P2\n" +
                "     regles:\n" +
                "       - id: 1\n" +
                "         type: presencezone\n" +
                "         zone: '606'\n" +
                "         presence: true\n" +
                "       - id: 2\n" +
                "         type: dependance\n" +
                "         zone: '606'\n" +
                "         souszone: '3'\n" +
                "         type-notice-liee: AUTORITE\n" +
                "         position: -1\n" +
                "       - id: 3\n" +
                "         type: presencezone\n" +
                "         zone: '200'\n" +
                "         presence: true\n";

        this.mockMvc.perform(post("/api/v1/indexComplexRules")
                .contentType("application/x-yaml").characterEncoding(StandardCharsets.UTF_8)
                .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test création règle complexe avec bornes de dépendance en minuscules")
    void testIndexComplexRuleDependencyWithLowercasePositionBounds() throws Exception {
        String yaml =
                "---\n" +
                "rules:\n" +
                "   - id: 2\n" +
                "     id-excel: 2\n" +
                "     message: test dépendance avec bornes\n" +
                "     priorite: P2\n" +
                "     regles:\n" +
                "       - id: 1\n" +
                "         type: presencezone\n" +
                "         zone: '606'\n" +
                "         presence: true\n" +
                "       - id: 2\n" +
                "         type: dependance\n" +
                "         zone: '606'\n" +
                "         souszone: '3'\n" +
                "         type-notice-liee: AUTORITE\n" +
                "         positionstart: 1\n" +
                "         positionend: -1\n" +
                "       - id: 3\n" +
                "         type: presencezone\n" +
                "         zone: '200'\n" +
                "         presence: true\n";

        this.mockMvc.perform(post("/api/v1/indexComplexRules")
                        .contentType("application/x-yaml").characterEncoding(StandardCharsets.UTF_8)
                        .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test creation regle complexe avec groupe meme zone imbrique")
    void testIndexComplexRuleWithNestedSameZoneGroup() throws Exception {
        String yaml =
                "---\n" +
                "rules:\n" +
                "   - id: 9200\n" +
                "     id-excel: 95555\n" +
                "     message: test groupe meme zone\n" +
                "     priorite: P1\n" +
                "     regles:\n" +
                "       - id: 9201\n" +
                "         type: presencechainecaracteres\n" +
                "         zone: '215'\n" +
                "         souszone: 'a'\n" +
                "         type-de-verification: CONTIENT\n" +
                "         chaines-caracteres:\n" +
                "           - chaine-caracteres: microfiche\n" +
                "       - id: 9202\n" +
                "         type: groupememezone\n" +
                "         zone: '328'\n" +
                "         operateur-booleen: ET\n" +
                "         regles:\n" +
                "           - id: 9203\n" +
                "             type: positionsouszone\n" +
                "             souszone: 'z'\n" +
                "             positions:\n" +
                "               - position: 1\n" +
                "                 comparateur: DIFFERENT\n" +
                "             operateur: OU\n";

        this.mockMvc.perform(post("/api/v1/indexComplexRules")
                        .contentType("application/x-yaml").characterEncoding(StandardCharsets.UTF_8)
                        .content(yaml).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    private ResultAction waitForCompletedResult(int id) throws Exception {
        MvcResult result = null;
        for (int i = 0; i < 200; i++) {
            result = this.mockMvc.perform(get("/api/v1/result/" + id)
                    .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8))
                    .andReturn();
            if (result.getResponse().getStatus() != HttpStatus.ACCEPTED.value()) {
                return new ResultAction(result);
            }
            Thread.sleep(50);
        }
        return new ResultAction(result);
    }

    private static class ResultAction {
        private final MvcResult mvcResult;

        private ResultAction(MvcResult mvcResult) {
            this.mvcResult = mvcResult;
        }

        private ResultAction andExpect(org.springframework.test.web.servlet.ResultMatcher matcher) throws Exception {
            matcher.match(this.mvcResult);
            return this;
        }
    }
}
