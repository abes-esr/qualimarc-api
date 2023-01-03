package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalAnalyse;
import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JournalService {
    @Autowired
    private JournalAnalyseRepository repository;

    public void addAnalyseIntoJournal(JournalAnalyse journalAnalyse) {
        repository.save(journalAnalyse);
    }
}
