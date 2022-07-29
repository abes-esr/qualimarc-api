package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.reference.RuleSet;
import fr.abes.qualimarc.core.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferenceService {
    public List<FamilleDocument> getTypesDocuments() {
        return Constants.TYPE_DOCUMENT;
    }

    public List<RuleSet> getTypesAnalyses() {
        return Constants.RULE_SET;
    }
}
