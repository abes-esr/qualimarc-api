package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "RULE_NOMBRESOUSZONES")
@Getter @Setter
@NoArgsConstructor
public class NombreSousZones extends Rule implements Serializable {
    //les sous zones doivent être renseignées sans le $
    @Column(name = "SOUS_ZONE")
    @NotNull
    private String sousZone;

    @Column(name = "ZONECIBLE")
    @NotNull
    private String zoneCible;

    @Column(name = "SOUS_ZONECIBLE")
    @NotNull
    private String sousZoneCible;

    public NombreSousZones(Integer id, String message, String zone, Priority priority, String sousZone, String zoneCible, String sousZoneCible) {
        super(id, message, zone, priority);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    public NombreSousZones(Integer id, String message, String zone, Priority priority, Set<FamilleDocument> famillesDocuments, String sousZone, String zoneCible, String sousZoneCible) {
        super(id, message, zone, priority, famillesDocuments);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    public NombreSousZones(String message, String zone, Priority priority, String sousZone, String zoneCible, String sousZoneCible) {
        super(message, zone, priority);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    public NombreSousZones(String message, String zone, Priority priority, Set<FamilleDocument> famillesDocuments, String sousZone, String zoneCible, String sousZoneCible) {
        super(message, zone, priority, famillesDocuments);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    @Override
    public boolean isValid(NoticeXml notice) {
        List<Datafield> zonesSource = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).collect(Collectors.toList());
        List<Long> nbSousZonesSourcePerZone = new ArrayList<>();
        zonesSource.stream().mapToLong(z -> z.getSubFields().stream().filter(ss -> ss.getCode().equals(this.sousZone)).count()).forEach(nbSousZonesSourcePerZone::add);

        List<Datafield> zonesCible = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZoneCible())).collect(Collectors.toList());
        List<Long> nbSousZonesCiblePerZone = new ArrayList<>();
        zonesCible.stream().mapToLong(z -> z.getSubFields().stream().filter(ss -> ss.getCode().equals(this.sousZoneCible)).count()).forEach(nbSousZonesCiblePerZone::add);
        if (nbSousZonesSourcePerZone.stream().reduce(0L, Long::sum) != nbSousZonesCiblePerZone.stream().reduce(0L, Long::sum))
            return true;
        return false;
    }
}
