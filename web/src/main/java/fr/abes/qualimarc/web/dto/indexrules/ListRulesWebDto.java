package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonRootName(value="rules")
public class ListRulesWebDto {
    private List<ComplexRuleWebDto> rules;

    public ListRulesWebDto() {
        this.rules = new ArrayList<>();
    }
}
