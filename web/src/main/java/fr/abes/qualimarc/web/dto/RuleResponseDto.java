package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;

@Getter
public class RuleResponseDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("zoneunm1")
    private String zoneUnm1;

    @JsonProperty("zoneunm2")
    private String zoneUnm2;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("message")
    private String message;

    public RuleResponseDto(Integer id, String zoneUnm1, String zoneUnm2, String priority, String message) {
        this.id = id;
        this.zoneUnm1 = zoneUnm1;
        this.zoneUnm2 = zoneUnm2;
        this.priority = priority;
        this.message = message;
    }
}
