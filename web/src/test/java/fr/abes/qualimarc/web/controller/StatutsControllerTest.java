package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.service.StatutsService;
import fr.abes.qualimarc.web.configuration.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {StatutsController.class}) //  Active le Model-View-Controller, nécessaire pour éviter le code d'erreur 415 lors du lancement du test checkPpn
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {WebConfig.class})
public class StatutsControllerTest {
    @Autowired
    WebApplicationContext context;

    @InjectMocks
    StatutsController statutsController;

    @MockBean
    StatutsService service;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(context.getBean(StatutsController.class)).build();
    }

    @Test
    void testGetStatuts() throws Exception {
        Mockito.when(service.getStatutBaseXml()).thenReturn(true);
        Mockito.when(service.getStatutBaseQualimarc()).thenReturn(false);
        Mockito.when(service.getDateLastPpnSynchronised()).thenReturn("14/12/2022 14:43:10");

        mockMvc.perform(get("/api/v1/statuts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutBaseXml").value("OK"))
                .andExpect(jsonPath("$.statutBaseQualimarc").value("NOK"))
                .andExpect(jsonPath("$.dateDerniereSynchronisation").value("14/12/2022 14:43:10"));

    }


}
