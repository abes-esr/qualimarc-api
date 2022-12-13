package fr.abes.qualimarc.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.configuration.WebConfig;
import fr.abes.qualimarc.web.exception.ExceptionControllerHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ReferenceController.class, UtilsMapper.class})
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {WebConfig.class})
public class ReferenceControllerTest {
    @Autowired
    WebApplicationContext context;

    @InjectMocks
    ReferenceController referenceController;

    @Autowired
    MappingJackson2HttpMessageConverter jsonHttpConverter;

    @Autowired
    MappingJackson2HttpMessageConverter yamlHttpConverter;

    MockMvc mockMvc;

    @MockBean
    ReferenceService referenceService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UtilsMapper utilsMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(context.getBean(ReferenceController.class))
                .setMessageConverters(this.yamlHttpConverter, this.jsonHttpConverter)
                .setControllerAdvice(new ExceptionControllerHandler())
                .build();
    }

    @Test
    void getFamillesDocuments() throws Exception {
        List<FamilleDocument> familles = new ArrayList<>();
        familles.add(new FamilleDocument("A", "Monographie"));
        familles.add(new FamilleDocument("F", "Manuscrit"));
        Mockito.when(referenceService.getTypesDocuments()).thenReturn(familles);

        this.mockMvc.perform(get("/api/v1/getFamillesDocuments")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[0].id").value("A"))
                .andExpect(jsonPath("$.[0].libelle").value("Monographie"))
                .andExpect(jsonPath("$.[1].id").value("F"))
                .andExpect(jsonPath("$.[1].libelle").value("Manuscrit"))
                .andExpect(jsonPath("$.[2].id").value("REPRO"))
                .andExpect(jsonPath("$.[2].libelle").value("Thèse de reproduction"))
                .andExpect(jsonPath("$.[3].id").value("SOUT"))
                .andExpect(jsonPath("$.[3].libelle").value("Thèse de soutenance"))
                .andReturn();
    }

    @Test
    void testIndexRulesSet() throws Exception {
        String yaml =
                "rulesets:\n" +
                "    - id:          1\n" +
                "      libelle:     test\n" +
                "      description: descriptiontest\n" +
                "      position:    0\n" +
                "    - id:          2\n" +
                "      libelle:     test1\n" +
                "      description: descriptiontest1\n" +
                "      position:    1\n";

        Mockito.doNothing().when(referenceService).saveAllRuleSets(Mockito.any());

        this.mockMvc.perform(post("/api/v1/indexRuleSet")
                .contentType("text/yml").characterEncoding(StandardCharsets.UTF_8)
                .content(yaml).characterEncoding(StandardCharsets.UTF_8)).andExpect(status().isOk());
    }
}
