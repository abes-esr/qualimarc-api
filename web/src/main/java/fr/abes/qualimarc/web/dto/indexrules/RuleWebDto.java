package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RuleWebDto {
    @JsonCreator
    public RuleWebDto() {
        super();
    }

}
