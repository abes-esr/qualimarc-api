package fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "RULE_RECIPROCITE")
@NoArgsConstructor
public class Reciprocite extends SimpleRule implements Serializable {

    private String sousZoneCible;

    public Reciprocite(Integer id, String zone, String sousZoneCible) {
        super(id, zone);
        this.sousZoneCible = sousZoneCible;
    }

    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml noticeMere = notices[0];
        NoticeXml noticeLiee = notices[1];
        boolean isPpnFound = true;
        List<Datafield> datafields = noticeLiee.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.zone)).collect(Collectors.toList());
        for (Datafield datafield : datafields) {
            List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZoneCible)).collect(Collectors.toList());
            isPpnFound = subFields.stream().noneMatch(subField -> subField.getValue().equals(noticeMere.getPpn()));
        }
        return isPpnFound;
    }

    @Override
    public String getZones() {
        return this.zone + "$" + this.sousZoneCible;
    }
}
