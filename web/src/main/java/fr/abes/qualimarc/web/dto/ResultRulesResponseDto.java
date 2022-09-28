package fr.abes.qualimarc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultRulesResponseDto {
    @JsonProperty("ppn")
    private String ppn;

    @JsonProperty("typeDocument")
    private String typeDocument;

    @JsonProperty("titre")
    private String titre;

    @JsonProperty("auteur")
    private String auteur;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("messages")
    private List<String> messages;

    @JsonProperty("detailerreurs")
    private List<RuleResponseDto> detailerreurs;

    public ResultRulesResponseDto() {
        this.messages = new ArrayList<>();
        this.detailerreurs = new ArrayList<>();
    }

    public ResultRulesResponseDto(String ppn,String typeDocument, List<String> messages) {
        this.ppn = ppn;
        this.typeDocument = typeDocument;
        this.messages = messages;
        this.detailerreurs = new ArrayList<>();
    }

    public void addDetailErreur(RuleResponseDto ruleResponseDto){
        this.detailerreurs.add(ruleResponseDto);
    }
}
