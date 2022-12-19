package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

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
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

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

        //cas ou on veut que la zone soit présente dans la notice pour lever le message
        if(this.isPresent) {
            if (this.getComplexRule() != null && this.getComplexRule().isMemeZone()) {
                //  Collecte dans une liste toutes les zones qui MATCHENT avec la zone cible
                this.getComplexRule().setSavedZone(
                    notice.getDatafields().stream().filter(df -> df.getTag().equals(this.getZone())).collect(Collectors.toList())
                );
                return this.getComplexRule().isSavedZoneIsNotEmpty();
            } else {
                return notice.getDatafields().stream().anyMatch(dataField -> dataField.getTag().equals(this.getZone()));
            }
        } else {
            //cas ou on veut que la zone soit absente de la notice pour lever le message
            if (this.getComplexRule() != null && this.getComplexRule().isMemeZone()) {
                this.getComplexRule().setSavedZone(
                    //  Collecte dans une liste toutes les zones qui NE MATCHENT PAS avec la zone cible
                    notice.getDatafields().stream().filter(df -> !df.getTag().equals(this.getZone())).collect(Collectors.toList())
                );
                return this.getComplexRule().isSavedZoneIsNotEmpty();
            } else {
                return notice.getDatafields().stream().noneMatch(dataField -> dataField.getTag().equals(this.getZone()));
            }
        }
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone);
        return listZones;
    }

}
