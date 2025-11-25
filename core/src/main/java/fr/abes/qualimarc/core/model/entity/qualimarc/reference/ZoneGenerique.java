package fr.abes.qualimarc.core.model.entity.qualimarc.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
