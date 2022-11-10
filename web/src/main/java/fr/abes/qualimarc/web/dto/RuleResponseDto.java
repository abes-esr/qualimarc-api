package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class RuleResponseDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("zones")
    private Set<String> zones;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("message")
    private String message;

    public RuleResponseDto(Integer id, String priority, String message) {
        this.id = id;
        this.priority = priority;
        this.message = message;
        this.zones = new HashSet<>();
    }

    public void addZone(String zone) {
        this.zones.add(zone);
    }
}
