package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.service.StatutsService;
import fr.abes.qualimarc.web.dto.statut.StatutsWebDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Slf4j
public class StatutsController {
    @Autowired
    private StatutsService service;

    @GetMapping(value = "/statusApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatutsWebDto getStatusApplication() {
        String statutBaseXml = (service.getStatutBaseXml()) ? "OK" : "NOK";
        String statutBaseQualimarc = (service.getStatutBaseQualimarc()) ? "OK" : "NOK";
        return new StatutsWebDto(statutBaseXml, statutBaseQualimarc, service.getDateLastPpnSynchronised());
    }

}
