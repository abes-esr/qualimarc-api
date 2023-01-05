package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalAnalyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalAnalyseRepository extends JpaRepository<JournalAnalyse, Integer> {
}
