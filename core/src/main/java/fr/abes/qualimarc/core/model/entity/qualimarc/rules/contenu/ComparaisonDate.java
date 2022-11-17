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

    private String zoneCible;

    private String sousZoneCible;

    private Integer position = 0;

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
                        switch (this.getOperateur()) {
                            case EGAL:
                                isOk = subField.getValue().substring(this.getPosition()).equals(subFieldCible.getValue().substring(this.getPosition()));
                                break;
                            case SUPERIEUR:
                                isOk = subField.getValue().substring(this.getPosition()).compareTo(subFieldCible.getValue().substring(this.getPosition())) > 0;
                                break;
                            case INFERIEUR:
                                isOk = subField.getValue().substring(this.getPosition()).compareTo(subFieldCible.getValue().substring(this.getPosition())) < 0;
                                break;
                            case INFERIEUR_EGAL:
                                isOk = subField.getValue().substring(this.getPosition()).compareTo(subFieldCible.getValue().substring(this.getPosition())) <= 0;
                                break;
                            case SUPERIEUR_EGAL:
                                isOk = subField.getValue().substring(this.getPosition()).compareTo(subFieldCible.getValue().substring(this.getPosition())) >= 0;
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
