package fr.abes.qualimarc.core.repository.basexml;

import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.basexml.NoticesBibio;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Optional;

@BaseXMLConfiguration
@Repository
public interface NoticesBibioRepository extends JpaRepository<NoticesBibio, Integer> {
    Optional<NoticesBibio> getByPpn(String ppn);

    @QueryHints(@QueryHint(name = "jakarta.persistence.query.timeout", value = "2000"))
    NoticesBibio findFirstByDateEtatBeforeOrderByDateEtatDesc(Calendar date);
}
