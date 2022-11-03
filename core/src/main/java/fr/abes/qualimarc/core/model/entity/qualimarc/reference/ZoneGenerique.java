package fr.abes.qualimarc.core.model.entity.qualimarc.reference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ZONE_GENERIQUE")
@Getter
@Setter
@NoArgsConstructor
public class ZoneGenerique implements Serializable {
    @Id
    @Column(name = "ZONE")
    private String zone;

    @Column(name = "ZONE_GENERIQUE")
    private String zoneGenerique;

    public ZoneGenerique(String zone, String zoneGenerique){
        this.zone = zone;
        this.zoneGenerique = zoneGenerique;
    }
}
