package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.utils.TypeNoticeLiee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.ArrayList;
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

    private Integer positionSousZoneSource;

    public DependencyRule(Integer id, String zoneSource, String sousZoneSource, TypeNoticeLiee typeNoticeLiee,Integer positionSousZoneSource, Integer position , ComplexRule complexRule) {
        super(id, position, complexRule);
        this.zoneSource = zoneSource;
        this.sousZoneSource = sousZoneSource;
        this.typeNoticeLiee = typeNoticeLiee;
        this.positionSousZoneSource = positionSousZoneSource;
    }
    public Set<String> getPpnsNoticeLiee(NoticeXml notice) {
        Set<String> listPpn = new HashSet<>();
        List<Datafield> datafields = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.zoneSource)).collect(Collectors.toList());
        for (Datafield datafield : datafields) {
            List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZoneSource)).collect(Collectors.toList());

            if(!subFields.isEmpty()) {
                if (positionSousZoneSource == null){
                    listPpn.addAll(subFields.stream().map(SubField::getValue).collect(Collectors.toSet()));
                } else if (positionSousZoneSource == -1){
                    listPpn.add(subFields.get(subFields.size() - 1).getValue());
                } else if( positionSousZoneSource <= subFields.size() ) {
                    listPpn.add(subFields.get(this.positionSousZoneSource - 1).getValue());
                }
            }
        }
        return listPpn;
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zoneSource + "$" + this.sousZoneSource);
        return listZones;
    }
}
