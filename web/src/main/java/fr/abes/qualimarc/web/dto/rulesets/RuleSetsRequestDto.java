package fr.abes.qualimarc.web.dto.rulesets;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@JsonRootName(value="rules")
public class RuleSetsRequestDto {
    List<RuleSetWebDto> ruleSetWebDtos;
}
