package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RuleResponseDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("zones")
    private List<String> zones;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("message")
    private String message;

    public RuleResponseDto(Integer id, String priority, String message) {
        this.id = id;
        this.priority = priority;
        this.message = message;
        this.zones = new ArrayList<>();
    }

    public void addZone(String zone) {
        this.zones.add(zone);
    }
}
