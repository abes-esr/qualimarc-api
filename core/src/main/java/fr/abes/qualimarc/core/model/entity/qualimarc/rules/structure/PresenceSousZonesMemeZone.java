package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.souszoneoperator.SousZoneOperator;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_PRESENCESOUSZONEMEMEZONE")
public class PresenceSousZonesMemeZone extends SimpleRule implements Serializable {

    @OneToMany(mappedBy = "presenceSousZonesMemeZone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SousZoneOperator> sousZoneOperators;

    public PresenceSousZonesMemeZone(Integer id, String zone, List<SousZoneOperator> sousZoneOperators) {
        super(id, zone);
        this.sousZoneOperators = sousZoneOperators;
    }

    public PresenceSousZonesMemeZone(Integer id, String zone) {
        super(id, zone);
        this.sousZoneOperators = new LinkedList<>();
    }

    public void addSousZoneOperator(SousZoneOperator sousZoneOperator){
        this.sousZoneOperators.add(sousZoneOperator);
    }

    @Override
    public boolean isValid(NoticeXml... notices) {
        if (notices.length > 0) {
            NoticeXml notice = Arrays.stream(notices).findFirst().get();
            List<Datafield> datafields = notice.getDatafields().stream().filter(dataField -> dataField.getTag().equals(this.getZone())).collect(Collectors.toList());
            boolean isOk;
            for (Datafield datafield : datafields) {
                if (this.sousZoneOperators.get(0).isPresent())
                    isOk = datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.sousZoneOperators.get(0).getSousZone()));
                else
                    isOk = datafield.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.sousZoneOperators.get(0).getSousZone()));

                for (int i = 1; i < this.sousZoneOperators.size(); i++) {
                    SousZoneOperator sousZoneOperator = this.sousZoneOperators.get(i);
                    if (sousZoneOperator.getOperateur().equals(BooleanOperateur.OU)) {
                        if (sousZoneOperator.isPresent())
                            isOk |= datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(sousZoneOperator.getSousZone()));
                        else
                            isOk |= datafield.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(sousZoneOperator.getSousZone()));
                    } else {
                        if (sousZoneOperator.isPresent())
                            isOk &= datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(sousZoneOperator.getSousZone()));
                        else
                            isOk &= datafield.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(sousZoneOperator.getSousZone()));
                    }
                }
                if (isOk)
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getZones() {
        StringBuilder zones = new StringBuilder("");
        for (int i =0; i < sousZoneOperators.size(); i++) {
            zones.append(this.getZone());
            zones.append("$");
            zones.append(sousZoneOperators.get(i).getSousZone());
            if(i < (sousZoneOperators.size()-1))
                zones.append("/");
        }
        return zones.toString();
    }
}
