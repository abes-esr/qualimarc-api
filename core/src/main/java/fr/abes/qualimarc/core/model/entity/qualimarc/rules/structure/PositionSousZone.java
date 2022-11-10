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
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_POSITIONSOUSZONE")
public class PositionSousZone extends SimpleRule implements Serializable {
    @Column(name = "SOUS_ZONE")
    private String sousZone;
    @Column(name = "POSITION")
    private Integer position;

    public PositionSousZone(Integer id, String zone, String sousZone, Integer position) {
        super(id, zone);
        this.sousZone = sousZone;
        this.position = position;
    }

    @Override
    public boolean isValid(NoticeXml notice) {
        //récupération de toutes les zones définies dans la règle
        List<Datafield> zones = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).collect(Collectors.toList());
        //vérification pour chaque zone répétée
        for (Datafield zone : zones) {
            //si la position de la sous zone n'est pas la position de la règle, la règle n'est pas valide
            if (zone.getSubFields().stream().map(sf -> sf.getCode()).collect(Collectors.toList()).indexOf(sousZone) != (position -1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }
}
