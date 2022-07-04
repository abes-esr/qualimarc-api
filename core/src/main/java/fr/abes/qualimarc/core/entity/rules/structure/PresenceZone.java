package fr.abes.qualimarc.core.entity.rules.structure;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.entity.rules.Rule;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PresenceZone extends Rule {

    private boolean isPresent;

    public PresenceZone(Integer id, String message, String zone, boolean isPresent) {
        super(id, message, zone);
        this.isPresent = isPresent;
    }

    @Override
    protected boolean isValid(NoticeXml notice) {
        if(this.isPresent) {
            return notice.getDatafields().stream().anyMatch(dataField -> dataField.getTag().equals(this.getZone()));
        }
        return notice.getDatafields().stream().noneMatch(dataField -> dataField.getTag().equals(this.getZone()));
    }

}
