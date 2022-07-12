package fr.abes.qualimarc.core.model.entity.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PresenceZone extends Rule {

    private boolean isPresent;

    public PresenceZone(Integer id, String message, String zone, boolean isPresent) {
        super(id, message, zone);
        this.isPresent = isPresent;
    }

    public PresenceZone(Integer id, String message, String zone, List<String> typeDocuments, boolean isPresent) {
        super(id, message, zone, typeDocuments);
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
