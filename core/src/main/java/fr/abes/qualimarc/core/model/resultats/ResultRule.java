package fr.abes.qualimarc.core.model.resultats;

import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResultRule {

    private Integer id;

    private List<String> zonesUnm;

    private Priority priority;

    private String message;

    public ResultRule(Integer id, Priority priority, String message) {
        this.id = id;
        this.zonesUnm = new ArrayList<>();
        this.priority = priority;
        this.message = message;
    }

    public void addZone(String zone) {
        this.zonesUnm.add(zone);
    }


}

