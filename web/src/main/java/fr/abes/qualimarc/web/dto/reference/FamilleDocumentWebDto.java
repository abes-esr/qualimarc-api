package fr.abes.qualimarc.web.dto.reference;


import com.fasterxml.jackson.annotation.JsonProperty;

public class FamilleDocumentWebDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("libelle")
    private String libelle;
}
