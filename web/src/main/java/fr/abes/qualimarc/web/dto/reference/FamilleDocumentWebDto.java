package fr.abes.qualimarc.web.dto.reference;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilleDocumentWebDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("libelle")
    private String libelle;
}
