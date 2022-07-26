package fr.abes.qualimarc.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ControllingPpnWithRuleSetsRequestDto {

    private List<String> ppnList;
    private List<String> rulesSetList;

    public ControllingPpnWithRuleSetsRequestDto(List<String> ppnList, List<String> rulesSetList) {
        this.ppnList = ppnList;
        this.rulesSetList = rulesSetList;
    }
}
