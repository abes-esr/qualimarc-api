package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalAnalyse;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalFamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalMessages;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalRuleSet;
import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalFamilleRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalMessagesRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalRuleSetRepository;
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

    @Autowired
    private JournalFamilleRepository journalFamilleRepository;

    @Autowired
    private JournalRuleSetRepository journalRuleSetRepository;

    public void saveJournalAnalyse(JournalAnalyse journalAnalyse) {
        journalAnalyseRepository.save(journalAnalyse);
    }

    /**
     * Méthode permettant de sauvegarder une liste de messages d'erreur dans la table des stats. Vérifie d'abord si le message est présent pour le mois en cours
     * @param journalMessages liste des messages à sauvegarder
     */
    public synchronized void saveJournalMessages(List<JournalMessages> journalMessages) {
        journalMessages.forEach(sm -> {
            JournalMessages newJournalMessage;
            Optional<JournalMessages> existingMessage = journalMessagesRepository.findByAnneeAndMoisAndMessage(sm.getAnnee(), sm.getMois(), sm.getMessage());
            if (existingMessage.isPresent()) {
                //le message est trouvé dans la table pour le mois et l'année en cours, on incrémente juste le nombre d'occurrences
                newJournalMessage = existingMessage.get();
                newJournalMessage.addOccurrence(sm.getOccurrences());
            } else {
                //message non trouvé, on l'ajoute à la table
                newJournalMessage = new JournalMessages(sm.getAnnee(), sm.getMois(), sm.getMessage(), sm.getOccurrences());
            }
            this.journalMessagesRepository.save(newJournalMessage);
        });
    }

    public void saveJournalFamille(JournalFamilleDocument journalFamilleDocument) {
        this.journalFamilleRepository.save(journalFamilleDocument);
    }

    public void saveJournalRuleSet(JournalRuleSet journalRuleSet) {
        this.journalRuleSetRepository.save(journalRuleSet);
    }
}
