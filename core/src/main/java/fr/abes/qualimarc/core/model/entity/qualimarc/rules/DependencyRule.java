package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class DependencyRule extends OtherRule {
    private String zoneSource;
    private String sousZoneSource;

    public DependencyRule(Integer id, String zoneSource, String sousZoneSource, Integer position, ComplexRule complexRule) {
        super(id, position, complexRule);
        this.zoneSource = zoneSource;
        this.sousZoneSource = sousZoneSource;
    }

    public List<String> getPpnsNoticeLiee(NoticeXml notice) {
        List<String> listPpn = new ArrayList<>();
        List<Datafield> datafields = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZoneSource())).collect(Collectors.toList());
        for (Datafield datafield : datafields) {
            List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZoneSource)).collect(Collectors.toList());
            if(!subFields.isEmpty()) {
                listPpn.add(subFields.get(0).getValue());
            }
        }
        return listPpn;
    }

    @Override
    public String getZones() {
        return this.getZoneSource() + "$" + this.getSousZoneSource();
    }
}
