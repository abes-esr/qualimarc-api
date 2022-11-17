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
import java.util.List;
import java.util.stream.Collectors;


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

    @Override
    public boolean isValid(NoticeXml notice) {
        List<Datafield> zonesSource = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).collect(Collectors.toList());
        List<Datafield> zonesSourceCible = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZoneCible())).collect(Collectors.toList());
        boolean isOk = false;
        for (Datafield zoneSource : zonesSource){
            for (Datafield zoneSourceCible : zonesSourceCible){
                for (SubField subField : zoneSource.getSubFields().stream().filter(ss -> ss.getCode().equals(this.getSousZone())).collect(Collectors.toList())) {
                    for (SubField subFieldCible : zoneSourceCible.getSubFields().stream().filter(ss -> ss.getCode().equals(this.getSousZoneCible())).collect(Collectors.toList())) {
                        //Controle de la zone pour verifier le contenu (date avec X ? date avec 4 chiffres ?)
                        String dateSourceString = subField.getValue();
                        String dateCibleString = subFieldCible.getValue();

                        if(this.getPositionStart() != null && this.getPositionEnd() != null)
                             dateSourceString = dateSourceString.substring(this.getPositionStart(), this.getPositionEnd());

                        if(this.getPositionStartCible() != null && this.getPositionEndCible() != null)
                            dateCibleString = dateCibleString.substring(this.getPositionStartCible(), this.getPositionEndCible());

                        if(dateSourceString.toUpperCase().contains("X") || dateCibleString.toUpperCase().contains("X") || dateSourceString.length() != 4 || dateCibleString.length() != 4)
                            return false;

                        Integer dateSource = Integer.parseInt(dateSourceString);
                        Integer dateCible = Integer.parseInt(dateCibleString);
                        switch (this.getOperateur()) {
                            case SUPERIEUR:
                                isOk = dateSource > dateCible;
                                break;
                            case INFERIEUR:
                                isOk = dateSource < dateCible;
                                break;
                            case EGAL:
                                isOk = dateSource.equals(dateCible);
                                break;
                            case SUPERIEUR_EGAL:
                                isOk = dateSource >= dateCible;
                                break;
                            case INFERIEUR_EGAL:
                                isOk = dateSource <= dateCible;
                                break;
                        }
                        if(isOk){
                            return true;
                        }
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
