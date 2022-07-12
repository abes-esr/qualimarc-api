package fr.abes.qualimarc.core.model.entity.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class Rule {
    private Integer id;
    private String message;
    private String zone;

    private List<String> typeDocuments;

    public Rule(Integer id, String message, String zone) {
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.typeDocuments = new ArrayList<>();
    }

    public Rule(Integer id, String message, String zone, List<String> typeDocuments){
        this.id = id;
        this.message = message;
        this.zone = zone;
        this.typeDocuments = typeDocuments;
    }

    public abstract boolean isValid(NoticeXml notice);

}
