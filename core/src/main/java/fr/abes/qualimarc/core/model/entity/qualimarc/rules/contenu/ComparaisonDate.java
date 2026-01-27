package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_COMPARAISONDATE")
public class ComparaisonDate extends SimpleRule implements Serializable {

    @Column(name = "SOUSZONE")
    @NotNull
    private String sousZone;

    @Column(name = "POSITIONSTART")
    private Integer positionStart;

    @Column(name = "POSITIONEND")
    private Integer positionEnd;

    @Column(name = "ZONECIBLE")
    @NotNull
    private String zoneCible;

    @Column(name = "SOUSZONECIBLE")
    @NotNull
    private String sousZoneCible;

    @Column(name = "POSITIONSTARTCIBLE")
    private Integer positionStartCible;

    @Column(name = "POSITIONENDCIBLE")
    private Integer positionEndCible;

    @Column(name = "OPERRATEUR")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ComparaisonOperateur comparateur;

    public ComparaisonDate(Integer id, String zone, Boolean affichageEtiquette,  String sousZone, Integer positionStart, Integer positionEnd, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible, ComparaisonOperateur comparateur) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.comparateur = comparateur;
    }

    public ComparaisonDate(Integer id, String zone, Boolean affichageEtiquette,  String sousZone, String zoneCible, String sousZoneCible, ComparaisonOperateur comparateur) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.comparateur = comparateur;
    }

    public ComparaisonDate(Integer id, String zone, Boolean affichageEtiquette, String sousZone, Integer positionStart, Integer positionEnd, String zoneCible, String sousZoneCible, ComparaisonOperateur comparateur) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.comparateur = comparateur;
    }

    public ComparaisonDate(Integer id, String zone, Boolean affichageEtiquette, String sousZone, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible, ComparaisonOperateur comparateur) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.comparateur = comparateur;
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
                    valueSource = valueSource.substring(this.getPositionStart(), this.getPositionEnd()+1);
                }
                if(this.getPositionEndCible() != null && valueCible.length() >= this.getPositionEndCible()){
                    valueCible = valueCible.substring(this.getPositionStartCible(), this.getPositionEndCible()+1);
                }

                //split to get juste number
                valueSource = (valueSource.split("\\D+").length > 0) ? valueSource.split("\\D+")[0] : null;
                valueCible = (valueCible.split("\\D+").length > 0) ? valueCible.split("\\D+")[0] : null;


                if((valueSource != null && valueCible != null) && (valueSource.length() == 4 && valueCible.length() == 4)) {
                    switch (this.getComparateur()) {
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
                        case DIFFERENT:
                            return !valueSource.equals(valueCible) ;
                        default:
                            return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone + "$" + this.sousZone);
        listZones.add(this.zoneCible + "$" + this.sousZoneCible);
        return listZones;
    }
}
