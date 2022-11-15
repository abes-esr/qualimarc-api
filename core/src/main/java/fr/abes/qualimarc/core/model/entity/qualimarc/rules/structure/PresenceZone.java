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
    public boolean isValid(NoticeXml notice) {
        //cas ou on veut que la zone soit présente dans la notice pour lever le message
        if(this.isPresent) {
            return notice.getDatafields().stream().anyMatch(dataField -> dataField.getTag().equals(this.getZone()));
        }
        //cas ou on veut que la zone soit absente de la notice pour lever le message
        return notice.getDatafields().stream().noneMatch(dataField -> dataField.getTag().equals(this.getZone()));
    }

    @Override
    public String getZones() {
        return this.getZone();
    }

}
