package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@JsonTypeName("typecaractere")
public class TypeCaractereWebDto extends SimpleRuleWebDto {
    @JsonProperty(value = "souszone")
    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "La sous zone est obligatoire")
    @NotBlank(message = "la sous zone ne peut pas être vide")
    private String sousZone;

    @JsonProperty("type-caracteres")
    @NotEmpty
    private List<String> typeCaracteres;
}

/*
 Simple Rule
 ---
 rules:
    - id: 1
      type:        typecaractere
      message:     aefmjampljfm
      zone:        603
      priorite:    P1
      type-doc:
      -    A
      -    B
      -    K
      souszone: a
      type-caracteres:
      - "ALPHABETIQUE"
      - "NUMERIQUE"
      - "SPECIAL"

Complex Rule
 ---
 rules:
   - id: 1
     id-excel: 1
     message: "fkjaeofhj"
     priorite: P2
     type-doc:
       - PC
     regles:
       - id: 2
         type: typecaractere
         zone: '463'
         souszone:    t
         type-caracteres:
         - "ALPHABETIQUE"
         - "NUMERIQUE"
         - "SPECIAL"
       - id: 3
         type: typecaractere
         zone: '305'
         souszone: x
         type-caracteres:
         - "ALPHABETIQUE"
 */
