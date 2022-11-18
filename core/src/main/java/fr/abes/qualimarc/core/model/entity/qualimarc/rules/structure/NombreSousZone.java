package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "RULE_NOMBRESOUSZONE")
@Getter
@Setter
@NoArgsConstructor
public class NombreSousZone extends SimpleRule implements Serializable {
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

    public NombreSousZone(Integer id, String zone, String sousZone, String zoneCible, String sousZoneCible) {
        super(id, zone);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml notice = notices[0];
        List<Datafield> zonesSource = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).collect(Collectors.toList());
        List<Long> nbSousZonesSourcePerZone = new ArrayList<>();
        zonesSource.stream().mapToLong(z -> z.getSubFields().stream().filter(ss -> ss.getCode().equals(this.sousZone)).count()).forEach(nbSousZonesSourcePerZone::add);

        List<Datafield> zonesCible = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZoneCible())).collect(Collectors.toList());
        List<Long> nbSousZonesCiblePerZone = new ArrayList<>();
        zonesCible.stream().mapToLong(z -> z.getSubFields().stream().filter(ss -> ss.getCode().equals(this.sousZoneCible)).count()).forEach(nbSousZonesCiblePerZone::add);
        return nbSousZonesSourcePerZone.stream().reduce(0L, Long::sum) != nbSousZonesCiblePerZone.stream().reduce(0L, Long::sum);
    }

    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone() + " / " + this.getZoneCible() + "$" + this.getSousZoneCible();
    }
}
