package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonRootName(value="rules")
public class ListRulesWebDto {
    private List<SimpleRuleWebDto> rules;

    public ListRulesWebDto() {
        this.rules = new ArrayList<>();
    }
}
