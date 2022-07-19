package fr.abes.qualimarc.core.model.entity.requestToCheck;

import fr.abes.qualimarc.core.model.entity.rules.rulesSet.RulesSetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FileToCheck {

    private String ppn;

    private List<RulesSetType> rulesSetType;

    public FileToCheck(String ppn, List<RulesSetType> rulesSetType) {
        this.ppn = ppn;
        this.rulesSetType = rulesSetType;
    }
}
