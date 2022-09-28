package fr.abes.qualimarc.core.model.resultats;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultRules {
    private String ppn;

    private FamilleDocument familleDocument;
    private List<String> messages;

    public ResultRules(String ppn) {
        this.ppn = ppn;
        this.messages = new ArrayList<>();
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
