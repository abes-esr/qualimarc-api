package fr.abes.qualimarc.web.dto.reference;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuleSetWebDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("libelle")
    private String libelle;
}
