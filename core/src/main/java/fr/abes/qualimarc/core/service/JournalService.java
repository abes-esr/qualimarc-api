package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalAnalyse;
import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.StatsMessages;
import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Méthode permettant de sauvegarder une liste de messages d'erreur dans la table des stats. Vérifie d'abord si le message est présent pour le mois en cours
     * @param statsMessages liste des messages à sauvegarder
     */
    public void saveStatsMessages(List<StatsMessages> statsMessages) {
        statsMessages.forEach(sm -> {
            StatsMessages newStatMessage;
            Optional<StatsMessages> existingMessage = journalMessagesRepository.findByAnneeAndMoisAndMessage(sm.getAnnee(), sm.getMois(), sm.getMessage());
            if (existingMessage.isPresent()) {
                //le message est trouvé dans la table pour le mois et l'année en cours, on incrémente juste le nombre d'occurrences
                newStatMessage = existingMessage.get();
                newStatMessage.addOccurrence(sm.getOccurrences());
            } else {
                //message non trouvé, on l'ajoute à la table
                newStatMessage = new StatsMessages(sm.getAnnee(), sm.getMois(), sm.getMessage(), sm.getOccurrences());
            }
            this.journalMessagesRepository.save(newStatMessage);
        });
    }
}
