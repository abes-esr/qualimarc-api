package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.StatsMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@QualimarcConfiguration
public interface JournalMessagesRepository extends JpaRepository<StatsMessages, Integer> {
    Optional<StatsMessages> findByAnneeAndMoisAndMessage(Integer annee, Integer mois, String message);
}
