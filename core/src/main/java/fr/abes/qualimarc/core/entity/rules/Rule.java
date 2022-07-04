package fr.abes.qualimarc.core.entity.rules;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public abstract class Rule {
    private Integer id;
    private String message;
    private String zone;

    public Rule(Integer id, String message, String zone) {
        this.id = id;
        this.message = message;
        this.zone = zone;
    }

    protected abstract boolean isValid(NoticeXml notice);

}
