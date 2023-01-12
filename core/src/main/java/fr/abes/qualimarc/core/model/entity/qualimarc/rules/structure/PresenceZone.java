package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_PRESENCEZONE")
public class PresenceZone extends SimpleRule implements Serializable {

    @Column(name = "IS_PRESENT")
    @NotNull
    private boolean isPresent;

    public boolean isPresent() {return isPresent;}

    public PresenceZone(Integer id, String zone, boolean isPresent) {
        super(id, zone);
        this.isPresent = isPresent;
    }

    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml notice = notices[0];

        List<Datafield> datafieldsValid;

        // On récupère les datafields qui correspondent à la zone
        datafieldsValid = notice.getDatafields().stream()
                .filter(datafield -> datafield.getTag().equals(this.getZone()))
                .collect(Collectors.toList());

        if(this.getComplexRule() != null && this.getComplexRule().isMemeZone()){
            // TODO: Il y a t'il un cas ou on verifie l'absence d'une zone dans une meme zone ???
            this.getComplexRule().setSavedZone(datafieldsValid);
            return this.getComplexRule().isSavedZoneIsNotEmpty();
        }

        return isPresent != datafieldsValid.isEmpty();

    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone);
        return listZones;
    }

}
