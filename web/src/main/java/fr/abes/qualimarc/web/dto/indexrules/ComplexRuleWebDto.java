package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonRootName(value="complexrule")
@Getter
@NoArgsConstructor
public class ComplexRuleWebDto {
    private List<LinkedRuleWebDto> complexeRule;
}
