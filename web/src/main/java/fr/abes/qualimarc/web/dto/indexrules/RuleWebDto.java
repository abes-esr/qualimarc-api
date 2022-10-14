package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.abes.qualimarc.web.dto.indexrules.structure.PresenceZoneWebDto;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes(
        {@JsonSubTypes.Type(value = SimpleRuleWebDto.class, name = "simplerule")}
)
@Data
public abstract class RuleWebDto {
}
