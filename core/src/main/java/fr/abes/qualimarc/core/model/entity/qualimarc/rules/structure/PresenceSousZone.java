package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_PRESENCESOUSZONE")
public class PresenceSousZone extends SimpleRule implements Serializable {
    @Column(name = "SOUS_ZONE")
    @NotNull
    private String sousZone;
    @Column(name = "IS_PRESENT")
    @NotNull
    private boolean isPresent;

    public PresenceSousZone(Integer id, String zone, String sousZone, boolean isPresent) {
        super(id, zone);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    public PresenceSousZone(Integer id, String zone, String sousZone, boolean isPresent, ComplexRule complexRule) {
        super(id, zone, complexRule);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml notice = notices[0];

        List<Datafield> datafields = notice.getDatafields().stream().filter(dataField -> dataField.getTag().equals(this.getZone())).collect(Collectors.toList());
        //cas ou la sous zone doit être présente dans la zone pour lever le message
        if(this.isPresent) {
            if (this.getComplexRule().isMemeZone()) {
                this.getComplexRule().setSavedZone(
                    //  Collecte dans une liste toutes les zones qui comportent des sousZones qui MATCHENT avec la sousZone cible
                    datafields.stream().filter(df -> df.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZone()))).collect(Collectors.toList())
                );
                return this.getComplexRule().isSavedZoneIsNotEmpty();
            } else {
                return datafields.stream().anyMatch(df -> df.getSubFields().stream().anyMatch(sf -> sf.getCode().equals(this.getSousZone())));
            }
        } else {
            //cas ou la sous zone doit être absente pour lever le message
            if (this.getComplexRule().isMemeZone()) {
                this.getComplexRule().setSavedZone(
                    //  Collecte dans une liste toutes les zones qui comportent des sousZones qui NE MATCHENT PAS avec la sousZone cible
                    datafields.stream().filter(df -> df.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.getSousZone()))).collect(Collectors.toList())
                );
                return this.getComplexRule().isSavedZoneIsNotEmpty();
            } else {
                return datafields.stream().noneMatch(datafield -> datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZone())));
            }
        }
    }

    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }
}

