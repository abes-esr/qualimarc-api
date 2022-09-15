package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListRulesWebDto {

    @JsonProperty(value = "rules")
    private List<RulesWebDto> listRulesWebDto; 
}
