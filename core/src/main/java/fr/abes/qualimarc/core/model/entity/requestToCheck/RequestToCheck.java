package fr.abes.qualimarc.core.model.entity.requestToCheck;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestToCheck {

    private List<String> ppnList;
    private List<String> rulesSetList;

    public RequestToCheck(List<String> ppnList, List<String> rulesSetList) {
        this.ppnList = ppnList;
        this.rulesSetList = rulesSetList;
    }
}
