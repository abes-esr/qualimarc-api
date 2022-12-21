package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@QualimarcConfiguration
public interface FamilleDocumentRepository extends JpaRepository<FamilleDocument, String> {
    List<FamilleDocument> findAllByRulesNotEmptyOrderByLibelle();
}
