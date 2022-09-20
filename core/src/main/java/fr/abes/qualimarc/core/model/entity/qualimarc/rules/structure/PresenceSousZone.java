package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_PRESENCESOUSZONE")
public class PresenceSousZone extends Rule implements Serializable {
    @Column(name = "SOUS_ZONE")
    @NotNull
    private String sousZone;
    @Column(name = "IS_PRESENT")
    @NotNull
    private boolean isPresent;

    public PresenceSousZone(Integer id, String message, String zone, String sousZone, Priority priority, boolean isPresent) {
        super(id, message, zone, priority);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    public PresenceSousZone(Integer id, String message, String zone, Priority priority, Set<FamilleDocument> familleDocuments, String sousZone, boolean isPresent) {
        super(id, message, zone, priority, familleDocuments);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    @Override
    public boolean isValid(NoticeXml notice) {

        List<Datafield> datafields = notice.getDatafields().stream().filter(dataField -> dataField.getTag().equals(this.getZone())).collect(Collectors.toList());
        //cas ou la sous zone doit être présente dans la zone pour lever le message
        if(this.isPresent) {
            for (Datafield datafield : datafields) {
                if (datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZone()))) {
                    return true;
                }
            }
        } else {
            //cas ou la sous zone doit être absente pour lever le message
            boolean absent = true;
            for (Datafield datafield : datafields) {
                if (!datafield.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.getSousZone()))) {
                    absent = false;
                }
            }
            return absent;
        }
        return false;
    }
}
