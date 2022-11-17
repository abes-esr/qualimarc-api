package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.utils.TypeNoticeLiee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class DependencyRule extends OtherRule {

    private String zoneSource;

    private String sousZoneSource;

    private TypeNoticeLiee typeNoticeLiee;


    public DependencyRule(Integer id, String zoneSource, String sousZoneSource, TypeNoticeLiee typeNoticeLiee, Integer position, ComplexRule complexRule) {
        super(id, position, complexRule);
        this.zoneSource = zoneSource;
        this.sousZoneSource = sousZoneSource;
        this.typeNoticeLiee = typeNoticeLiee;
    }

    public Set<String> getPpnsNoticeLiee(NoticeXml notice) {
        Set<String> listPpn = new HashSet<>();
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
