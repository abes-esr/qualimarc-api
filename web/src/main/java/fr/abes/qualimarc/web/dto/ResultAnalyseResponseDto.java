package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultAnalyseResponseDto {
    @JsonProperty("resultRules")
    private List<ResultRulesResponseDto> resultRules;
    @JsonProperty("ppnAnalyses")
    private List<String> ppnAnalyses;
    @JsonProperty("ppnErrones")
    private List<String> ppnErrones;
    @JsonProperty("ppnOk")
    private List<String> ppnOk;
    @JsonProperty("ppnInconnus")
    private List<String> ppnInconnus;

    public ResultAnalyseResponseDto() {
        this.resultRules = new ArrayList<>();
        this.ppnAnalyses = new ArrayList<>();
        this.ppnErrones = new ArrayList<>();
        this.ppnOk = new ArrayList<>();
        this.ppnInconnus = new ArrayList<>();
    }

    public void addResultRule(ResultRulesResponseDto result) {
        this.resultRules.add(result);
    }
}
