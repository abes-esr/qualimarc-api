package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalAnalyse;
import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.StatsMessages;
import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class JournalService {
    @Autowired
    private JournalAnalyseRepository journalAnalyseRepository;

    @Autowired
    private JournalMessagesRepository journalMessagesRepository;

    public void addAnalyseIntoJournal(JournalAnalyse journalAnalyse) {
        journalAnalyseRepository.save(journalAnalyse);
    }

    public void saveStatsMessages(List<StatsMessages> statsMessages) {
        statsMessages.forEach(sm -> {
            StatsMessages newStatMessage;
            Optional<StatsMessages> existingMessage = journalMessagesRepository.findByAnneeAndMoisAndMessage(sm.getAnnee(), sm.getMois(), sm.getMessage());
            if (existingMessage.isPresent()) {
                newStatMessage = existingMessage.get();
                newStatMessage.addOccurrence(sm.getOccurrences());
            } else {
                newStatMessage = new StatsMessages(sm.getAnnee(), sm.getMois(), sm.getMessage(), sm.getOccurrences());
            }
            this.journalMessagesRepository.save(newStatMessage);
        });
    }

    public void addStatMessage(String message) {
        Calendar today = Calendar.getInstance();
        StatsMessages statsMessages;
        Integer actualYear = today.get(Calendar.YEAR);
        Integer actualMonth = today.get(Calendar.MONTH) + 1;
        Optional<StatsMessages> existingMessage = journalMessagesRepository.findByAnneeAndMoisAndMessage(actualYear, actualMonth, message);
        if (existingMessage.isPresent()) {
            statsMessages = existingMessage.get();
            statsMessages.addOccurrence();
        } else {
            statsMessages = new StatsMessages(actualYear, actualMonth, message);
        }
        journalMessagesRepository.save(statsMessages);
    }

}
