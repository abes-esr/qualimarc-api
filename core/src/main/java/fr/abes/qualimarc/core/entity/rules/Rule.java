package fr.abes.qualimarc.core.entity.rules;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Rule {
    private Integer id;
    private String message;
    private String zone;

    protected abstract Boolean isValid(NoticeXml notice);

}
