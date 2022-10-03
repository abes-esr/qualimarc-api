package fr.abes.qualimarc.core.model.resultats;

import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ResultRule {

    private Integer id;

    private String zoneUnm1;

    @Setter
    private String zoneUnm2;

    private Priority priority;

    private String message;

    public ResultRule(Integer id, String zoneUnm1, Priority priority, String message) {
        this.id = id;
        this.zoneUnm1 = zoneUnm1;
        this.priority = priority;
        this.message = message;
    }


}

