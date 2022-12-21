package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.repository.qualimarc.FamilleDocumentRepository;
import fr.abes.qualimarc.core.repository.qualimarc.RuleSetRepository;
import fr.abes.qualimarc.core.repository.qualimarc.ZoneGeneriqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReferenceService {
    private FamilleDocumentRepository familleDocumentRepository;

    private RuleSetRepository ruleSetRepository;

    private ZoneGeneriqueRepository zoneGeneriqueRepository;

    @Autowired
    public ReferenceService(FamilleDocumentRepository familleDocumentRepository, RuleSetRepository ruleSetRepository, ZoneGeneriqueRepository zoneGeneriqueRepository) {
        this.familleDocumentRepository = familleDocumentRepository;
        this.ruleSetRepository = ruleSetRepository;
        this.zoneGeneriqueRepository = zoneGeneriqueRepository;
    }

    public List<FamilleDocument> getTypesDocuments() {
        List<FamilleDocument> resultsList = familleDocumentRepository.findAll();
        Collections.sort(resultsList);
        return resultsList;
    }

    public List<RuleSet> getRuleSets() {
        List<RuleSet> resultsList = ruleSetRepository.findAllByRulesNotEmpty();
        Collections.sort(resultsList);
        return resultsList;
    }

    public void viderRulesSet() {
        ruleSetRepository.deleteAll();
    }

    public FamilleDocument getFamilleDocument(String typeDocument) {
        Optional<FamilleDocument> familleDocument = familleDocumentRepository.findById(typeDocument);
        if (familleDocument.isPresent()) {
            return familleDocument.get();
        }
        throw new IllegalTypeDocumentException("La famille de document n'a pas pu être trouvée.");
    }

    public List<String> getZonesGeneriques(String zone) {
        return this.zoneGeneriqueRepository.getZoneGeneriqueZoneByZoneGenerique(zone);
    }

    public void saveAllRuleSets(List<RuleSet> ruleSetList){
        this.ruleSetRepository.saveAll(ruleSetList);
    }
}
