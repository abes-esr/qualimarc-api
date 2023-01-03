package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalMessagesRepository extends JpaRepository<JournalMessages, Integer> {
}
