package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalFamilleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JournalFamilleRepository extends JpaRepository<JournalFamilleDocument, Integer> {

    List<JournalFamilleDocument> findAllByDateTimeBetween(Date dateDebut, Date dateFin);

    void deleteAllByDateTimeBefore(Date date);
}
