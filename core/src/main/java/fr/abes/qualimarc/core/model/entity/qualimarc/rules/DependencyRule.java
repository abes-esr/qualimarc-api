package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class DependencyRule extends OtherRule {
    private String zoneSource;
    private String sousZoneSource;

    public DependencyRule(String sousZoneSource) {
        this.sousZoneSource = sousZoneSource;
    }

    public String getPpnNoticeLiee(NoticeXml notice) {
        List<Datafield> datafields = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZoneSource())).collect(Collectors.toList());
        for (Datafield datafield : datafields) {
            List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZoneSource)).collect(Collectors.toList());
            if (!subFields.isEmpty()) {
                return subFields.get(0).getValue();
            }
        }
        return null;
    }

    @Override
    public String getZones() {
        return this.getZoneSource() + "$" + this.getSousZoneSource();
    }
}
