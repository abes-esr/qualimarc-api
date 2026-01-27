package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.utils.TypeNoticeLiee;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Integer positionSousZoneSourceStart;

    private Integer positionSousZoneSourceEnd;

    public DependencyRule(Integer id, String zoneSource, String sousZoneSource, TypeNoticeLiee typeNoticeLiee,Integer positionSousZoneSource, Integer position , ComplexRule complexRule) {
        super(id, position, complexRule);
        this.zoneSource = zoneSource;
        this.sousZoneSource = sousZoneSource;
        this.typeNoticeLiee = typeNoticeLiee;
        this.positionSousZoneSourceStart = positionSousZoneSource;
        this.positionSousZoneSourceEnd = positionSousZoneSource;
    }

    public DependencyRule(Integer id, String zoneSource, String sousZoneSource, TypeNoticeLiee typeNoticeLiee, Integer positionSousZoneSource, Integer positionSousZoneSourceStart, Integer positionSousZoneSourceEnd, Integer position , ComplexRule complexRule) {
        super(id, position, complexRule);
        this.zoneSource = zoneSource;
        this.sousZoneSource = sousZoneSource;
        this.typeNoticeLiee = typeNoticeLiee;
        this.positionSousZoneSourceStart = positionSousZoneSource != null ? positionSousZoneSource : positionSousZoneSourceStart;
        this.positionSousZoneSourceEnd = positionSousZoneSource != null ? positionSousZoneSource : positionSousZoneSourceEnd;
    }

    public DependencyRule(Integer id, String zoneSource, String sousZoneSource, TypeNoticeLiee typeNoticeLiee,Integer positionSousZoneSourceStart, Integer positionSousZoneSourceEnd, Integer position , ComplexRule complexRule) {
        super(id, position, complexRule);
        this.zoneSource = zoneSource;
        this.sousZoneSource = sousZoneSource;
        this.typeNoticeLiee = typeNoticeLiee;
        this.positionSousZoneSourceStart = positionSousZoneSourceStart;
        this.positionSousZoneSourceEnd = positionSousZoneSourceEnd;
    }

    public Set<String> getPpnsNoticeLiee(NoticeXml notice) {
        Set<String> listPpn = new HashSet<>();
        List<Datafield> datafields = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.zoneSource)).collect(Collectors.toList());
        for (Datafield datafield : datafields) {
            List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZoneSource)).collect(Collectors.toList());

            if(!subFields.isEmpty()) {
                Integer startInterval = resolveIndex(this.positionSousZoneSourceStart, subFields.size(), 0);
                Integer endInterval = resolveIndex(this.positionSousZoneSourceEnd,   subFields.size(), subFields.size() - 1);
                if(startInterval != null && endInterval != null) {
                    if(startInterval <= endInterval) {
                        listPpn.addAll(subFields.subList(startInterval, endInterval + 1).stream().map(SubField::getValue).collect(Collectors.toSet()));
                    } else {
                        listPpn.addAll(subFields.subList(0, endInterval + 1).stream().map(SubField::getValue).collect(Collectors.toSet()));
                        listPpn.addAll(subFields.subList(startInterval, subFields.size()).stream().map(SubField::getValue).collect(Collectors.toSet()));
                    }
                }
            }
        }
        return listPpn;
    }

    private Integer resolveIndex(Integer configuredIndex, int size, int defaultIfNull) {
        if (configuredIndex == null) {
            return defaultIfNull;
        }
        if (configuredIndex < 0 && -configuredIndex <= size) {
            return size + configuredIndex;
        }
        if (configuredIndex < size && configuredIndex >= 0) {
            return configuredIndex;
        }
        return null;
    }


    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zoneSource + "$" + this.sousZoneSource);
        return listZones;
    }
}
