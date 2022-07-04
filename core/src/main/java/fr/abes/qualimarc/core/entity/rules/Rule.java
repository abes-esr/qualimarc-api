package fr.abes.qualimarc.core.entity.rules;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import lombok.Data;

@Data
public abstract class Rule {
    private Integer id;
    private String message;
    private String zone;

    protected abstract Boolean isValid(NoticeXml notice);

}
