package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalAnalyse;
import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalMessages;
import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalService {
    @Autowired
    private JournalAnalyseRepository journalAnalyseRepository;

    @Autowired
    private JournalMessagesRepository journalMessagesRepository;

    public void addAnalyseIntoJournal(JournalAnalyse journalAnalyse) {
        journalAnalyseRepository.save(journalAnalyse);
    }

    public void addMessageToJournal(String message, List<String> zones) {
        JournalMessages journalMessages = new JournalMessages(new Date(), message, zones.stream().collect(Collectors.joining("|")));
        journalMessagesRepository.save(journalMessages);
    }
}
