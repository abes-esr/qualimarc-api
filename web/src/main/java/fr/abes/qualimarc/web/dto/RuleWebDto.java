package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleWebDto {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("zoneUnm1")
    private String zoneUnm1;
    @JsonProperty("zoneUnm2")
    private String zoneUnm2;
    @JsonProperty("typeDoc")
    private String typeDoc;
    @JsonProperty("message")
    private String message;
    @JsonProperty("priority")
    private String priority;

}
