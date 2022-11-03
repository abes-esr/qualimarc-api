package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ReferenceController {
    @Autowired
    private ReferenceService service;

    @GetMapping("/getRuleSets")
    public List<RuleSet> getRuleSets() {
        return service.getRuleSets();
    }

    @GetMapping("/getFamillesDocuments")
    public List<FamilleDocument> getFamillesDocuments() {
        return service.getTypesDocuments();
    }
}
