package fr.abes.qualimarc.web.dto.statut;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatutsWebDto {
    @JsonProperty("statutBaseXml")
    private String statutBaseXml;
    @JsonProperty("statutBaseQualimarc")
    private String statutBaseQualimarc;
    @JsonProperty("dateDerniereSynchronisation")
    private String dateDerniereSynchronisation;
}
