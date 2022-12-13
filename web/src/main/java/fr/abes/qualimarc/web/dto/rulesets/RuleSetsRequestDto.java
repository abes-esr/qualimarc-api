package fr.abes.qualimarc.web.dto.rulesets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RuleSetsRequestDto {
    @JsonProperty("rulesets")
    private List<RuleSetWebDto> ruleSetWebDtos;
}
