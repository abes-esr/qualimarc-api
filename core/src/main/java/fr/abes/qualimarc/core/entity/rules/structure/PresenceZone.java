package fr.abes.qualimarc.core.entity.rules.structure;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.entity.rules.Rule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresenceZone extends Rule {

    private Boolean isPresent;

    public PresenceZone(Integer id, String message, String zone, Boolean isPresent) {
        super(id, message, zone);
        this.isPresent = isPresent;
    }

    @Override
    protected Boolean isValid(NoticeXml notice) {
        if(this.isPresent) {
            return notice.getDataFields().stream().anyMatch(dataField -> dataField.getTag().equals(this.getZone()));
        }
        return notice.getDataFields().stream().noneMatch(dataField -> dataField.getTag().equals(this.getZone()));
    }

}
