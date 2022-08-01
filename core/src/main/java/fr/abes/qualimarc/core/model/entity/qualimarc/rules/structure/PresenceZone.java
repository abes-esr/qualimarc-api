package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_PRESENCEZONE")
public class PresenceZone extends Rule {

    @Column(name = "IS_PRESENT")
    private boolean isPresent;


    public PresenceZone(Integer id, String message, String zone, Priority priority, boolean isPresent) {
        super(id, message, zone, priority);
        this.isPresent = isPresent;
    }

    public PresenceZone(Integer id, String message, String zone, Priority priority, Set<FamilleDocument> typeDocuments, boolean isPresent) {
        super(id, message, zone, priority, typeDocuments);
        this.isPresent = isPresent;
    }

    public PresenceZone(String message, String zone, Priority priority, boolean isPresent) {
        super(message, zone, priority);
        this.isPresent = isPresent;
    }

    public PresenceZone(String message, String zone, Priority priority, Set<FamilleDocument> famillesDocuments, boolean isPresent) {
        super(message, zone, priority, famillesDocuments);
        this.isPresent = isPresent;
    }

    @Override
    public boolean isValid(NoticeXml notice) {
        if(this.isPresent) {
            return notice.getDatafields().stream().anyMatch(dataField -> dataField.getTag().equals(this.getZone()));
        }
        return notice.getDatafields().stream().noneMatch(dataField -> dataField.getTag().equals(this.getZone()));
    }

}
