package fr.abes.qualimarc.core.model.resultats;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultRules {
    private String ppn;
    private List<String> messages;

    public ResultRules(String ppn) {
        this.ppn = ppn;
        this.messages = new ArrayList<>();
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
