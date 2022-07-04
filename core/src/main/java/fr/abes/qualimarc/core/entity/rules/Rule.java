package fr.abes.qualimarc.core.entity.rules;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class Rule {
    private Integer id;
    private String message;
    private String zone;

    private List<String> type_documents;

    public Rule(Integer id, String message, String zone) {
        this.id = id;
        this.message = message;
        this.zone = zone;
    }
    public Rule(Integer id, String message, String zone, List<String> typeDocuments){
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.type_documents = typeDocuments;
    }

    protected abstract boolean isValid(NoticeXml notice);

}
