package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@QualimarcConfiguration
public interface JournalMessagesRepository extends JpaRepository<JournalMessages, Integer> {
    Optional<JournalMessages> findByAnneeAndMoisAndMessage(Integer annee, Integer mois, String message);

    List<JournalMessages> findByAnneeAndMois(Integer annee, Integer mois);
}
