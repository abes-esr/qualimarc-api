package fr.abes.qualimarc.core.repository.basexml;

import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.basexml.NoticesAutorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@BaseXMLConfiguration
@Repository
public interface NoticesAutoriteRepository extends JpaRepository<NoticesAutorite, Integer> {
    Optional<NoticesAutorite> getByPpn(String ppn);
}
