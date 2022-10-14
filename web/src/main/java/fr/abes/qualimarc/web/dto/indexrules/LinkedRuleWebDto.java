package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LinkedRuleWebDto {
    @JsonProperty("simplerule")
    private SimpleRuleWebDto simpleRule;
    @JsonProperty("operateur")
    private BooleanOperateur operateur;
}
