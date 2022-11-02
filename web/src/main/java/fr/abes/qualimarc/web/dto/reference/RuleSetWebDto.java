package fr.abes.qualimarc.web.dto.reference;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RuleSetWebDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("libelle")
    private String libelle;
}
