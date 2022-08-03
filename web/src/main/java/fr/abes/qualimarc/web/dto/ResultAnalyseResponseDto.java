package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @JsonProperty("nbPpnAnalyses")
    private Integer nbPpnAnalyses;
    @JsonProperty("npPpnErrones")
    private Integer nbPpnErrones;
    @JsonProperty("nbPpnOk")
    private Integer nbPpnOk;
    @JsonProperty("nbPpnInconnus")
    private Integer nbPpnInconnus;

    public ResultAnalyseResponseDto() {
        this.resultRules = new ArrayList<>();
        this.ppnAnalyses = new ArrayList<>();
        this.ppnErrones = new ArrayList<>();
        this.ppnOk = new ArrayList<>();
        this.ppnInconnus = new ArrayList<>();
        this.nbPpnAnalyses = 0;
        this.nbPpnErrones = 0;
        this.nbPpnOk = 0;
        this.nbPpnInconnus = 0;
    }

    public void addResultRule(ResultRulesResponseDto result) {
        this.resultRules.add(result);
    }
}
