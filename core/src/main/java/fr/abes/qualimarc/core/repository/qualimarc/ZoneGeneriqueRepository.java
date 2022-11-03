package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.ZoneGenerique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@QualimarcConfiguration
public interface ZoneGeneriqueRepository extends JpaRepository<ZoneGenerique, String> {
    @Query("select z.zone from ZoneGenerique z where z.zoneGenerique=:zoneGenerique")
    List<String> getZoneGeneriqueZoneByZoneGenerique(@Param("zoneGenerique") String zoneGenerique);
}
