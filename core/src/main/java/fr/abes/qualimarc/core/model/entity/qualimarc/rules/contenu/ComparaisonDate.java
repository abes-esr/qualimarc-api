package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.Operateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_COMPARAISONDATE")
public class ComparaisonDate extends SimpleRule implements Serializable {

    private String sousZone;

    private Integer positionStart;

    private Integer positionEnd;

    private String zoneCible;

    private String sousZoneCible;

    private Integer positionStartCible;

    private Integer positionEndCible;

    private Operateur operateur;

    public ComparaisonDate(Integer id, String zone, String sousZone, Integer positionStart, Integer positionEnd, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible, Operateur operateur) {
        super(id, zone);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.operateur = operateur;
    }

    public ComparaisonDate(Integer id, String zone, String sousZone, String zoneCible, String sousZoneCible, Operateur operateur) {
        super(id, zone);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.operateur = operateur;
    }

    public ComparaisonDate(Integer id, String zone, String sousZone, Integer positionStart, Integer positionEnd, String zoneCible, String sousZoneCible, Operateur operateur) {
        super(id, zone);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.operateur = operateur;
    }

    public ComparaisonDate(Integer id, String zone, String sousZone, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible, Operateur operateur) {
        super(id, zone);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.operateur = operateur;
    }

    @Override
    public boolean isValid(NoticeXml... notices) {
        NoticeXml notice = notices[0];
        Datafield datafieldSource = notice.getDatafields().stream()
                .filter(datafield -> datafield.getTag().equals(this.getZone()) && datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZone())))
                .findFirst().orElse(null);
        Datafield datafieldCible = notice.getDatafields().stream()
                .filter(datafield -> datafield.getTag().equals(this.getZoneCible()) && datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZoneCible())))
                .findFirst().orElse(null);

        if (datafieldSource != null && datafieldCible != null) {
            SubField subFieldSource = datafieldSource.getSubFields().stream().filter(subField -> subField.getCode().equals(this.getSousZone())).findFirst().orElse(null);
            SubField subFieldCible = datafieldCible.getSubFields().stream().filter(subField -> subField.getCode().equals(this.getSousZoneCible())).findFirst().orElse(null);
            if (subFieldSource != null && subFieldCible != null) {
                String valueSource = subFieldSource.getValue();
                String valueCible = subFieldCible.getValue();
                if (this.getPositionEnd() != null && valueSource.length() >= this.getPositionEnd()) {
                    valueSource = valueSource.substring(this.getPositionStart(), this.getPositionEnd());
                }
                if(this.getPositionEndCible() != null && valueCible.length() >= this.getPositionEndCible()){
                    valueCible = valueCible.substring(this.getPositionStartCible(), this.getPositionEndCible());
                }

                //split to get juste number
                valueSource = valueSource.split("\\D+")[0];
                valueCible = valueCible.split("\\D+")[0];


                if(valueSource.length() == 4 && valueCible.length() == 4) {
                    switch (this.getOperateur()) {
                        case EGAL:
                            return valueSource.equals(valueCible);
                        case INFERIEUR:
                            return valueSource.compareTo(valueCible) < 0;
                        case SUPERIEUR:
                            return valueSource.compareTo(valueCible) > 0;
                        case INFERIEUR_EGAL:
                            return valueSource.compareTo(valueCible) <= 0;
                        case SUPERIEUR_EGAL:
                            return valueSource.compareTo(valueCible) >= 0;
                        default:
                            return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone() + "/" + this.getZoneCible() + "$" + this.getSousZoneCible();
    }
}
