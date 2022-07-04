package fr.abes.qualimarc.core.repository.basexml;

import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.entity.notice.NoticesBibio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@BaseXMLConfiguration
@Repository
public interface NoticesBibioRepository extends JpaRepository<NoticesBibio, Integer> {
    Optional<NoticesBibio> getByPpn(String ppn);
}
