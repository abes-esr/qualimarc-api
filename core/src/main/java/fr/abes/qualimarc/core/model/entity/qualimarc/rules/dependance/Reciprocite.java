package fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "RULE_RECIPROCITE")
@NoArgsConstructor
public class Reciprocite extends SimpleRule implements Serializable {

    private String sousZoneCible;

    public Reciprocite(Integer id, String zone, Boolean affichageEtiquette, String sousZoneCible) {
        super(id, zone, affichageEtiquette);
        this.sousZoneCible = sousZoneCible;
    }

    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml noticeMere = notices[0];
        NoticeXml noticeLiee = notices[1];
        List<Datafield> datafields = noticeLiee.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.zone) && datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.sousZoneCible))).collect(Collectors.toList());

        return datafields.stream().allMatch(datafield -> datafield.getSubFields().stream().noneMatch(subField -> subField.getValue().equals(noticeMere.getPpn())));
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone + "$" + this.sousZoneCible);
        return listZones;
    }
}
