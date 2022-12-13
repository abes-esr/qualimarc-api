package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
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
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml notice = notices[0];
        //récupération de toutes les zones définies dans la règle
        List<Datafield> datafieldList = notice.getDatafields().stream().filter(
                datafield -> datafield.getTag().equals(this.getZone())
        ).collect(Collectors.toList());

        if(datafieldList.isEmpty())
            return false;

        //récupération des zones qui ont la sous-zone a la bonne position
        List<Datafield> datafieldsValid = datafieldList.stream().filter(
                df -> df.getSubFields().stream().map(SubField::getCode).collect(Collectors.toList()).indexOf(sousZone) == (position -1)
        ).collect(Collectors.toList());

        if(this.getComplexRule() != null && this.getComplexRule().isMemeZone()){
            this.getComplexRule().setSavedZone(datafieldsValid);
            return this.getComplexRule().isSavedZoneIsNotEmpty();
        }
        return datafieldsValid.size() > 0;
    }

    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }
}
