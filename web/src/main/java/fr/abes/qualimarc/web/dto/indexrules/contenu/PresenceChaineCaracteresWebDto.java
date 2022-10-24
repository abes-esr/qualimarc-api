package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@JsonTypeName("presencechainecaracteres")
public class PresenceChaineCaracteresWebDto extends SimpleRuleWebDto {

    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    @JsonProperty("souszone")
    private String sousZone;

    @Pattern(regexp = "STRICTEMENT|CONTIENT|COMMENCE|TERMINE", message = "Le champ niveau de verification ne peut contenir que STRICTEMENT, CONTIENT, COMMENCE, TERMINE")
    @JsonProperty("type-de-verification")
    @NotNull
    private String typeDeVerification;

    @JsonProperty("autre-chaine-caracteres")
    private List<ChaineCaracteresWebDto> listChaineCaracteres;

    public PresenceChaineCaracteresWebDto(Integer id, Integer idExcel, String message, String zone, String priority, List<String> typesDoc, String sousZone, String typeDeVerifications) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerifications;
        this.listChaineCaracteres = new LinkedList<>();
    }

    public PresenceChaineCaracteresWebDto(Integer id, String zone, String booleanOperator, String sousZone, String typeDeVerification, List<ChaineCaracteresWebDto> listChaineCaracteres) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.listChaineCaracteres = new LinkedList<>();
    }

    public void addChaineCaracteres(ChaineCaracteresWebDto chaineCaracteresWebDto) {
        this.listChaineCaracteres.add(chaineCaracteresWebDto);
    }

    @Getter
    @NoArgsConstructor
    public static class ChaineCaracteresWebDto {
        @Pattern(regexp = "ET|OU", message = "L'opérateur doit être égal à OU ou à ET")
        @JsonProperty("operateur")
        private String operateur;

        @JsonProperty("chaine-caracteres")
        @NotNull
        private String chaineCaracteres;

        public ChaineCaracteresWebDto(String chaineCaracteres) {
            this.chaineCaracteres = chaineCaracteres;
        }

        public ChaineCaracteresWebDto(String operateur, String chaineCaracteres) {
            this.operateur = operateur;
            this.chaineCaracteres = chaineCaracteres;
        }
    }
}

/*
// SIMPLE
### Présence d'une chaine de caractères

* La vérification STRICTEMENT peut comporter :
    * soit une chaine-caracteres,
    * soit une chaine-caracteres et plusieurs autre-chaine-caracteres, mais chacune de ces autre-chaine-caracteres ne peut recevoir que l'opérateur OU
* La vérification COMMENCE peut comporter :
    * soit chaine-caracteres,
    * soit une chaine-caracteres et plusieurs autre-chaine-caracteres, mais chacune de ces autre-chaine-caracteres ne peut recevoir que l'opérateur OU
* La vérification TERMINE peut comporter :
    * soit chaine-caracteres,
    * soit une chaine-caracteres et plusieurs autre-chaine-caracteres, mais chacune de ces autre-chaine-caracteres ne peut recevoir que l'opérateur OU
* La vérification CONTIENT peut comporter :
    * soit chaine-caracteres,
    * soit une chaine-caracteres et plusieurs autre-chaine-caracteres, chacune de ces autre-chaine-caracteres pouvant recevoir l'opérateur ET ou OU

``` YAML
rules:
    - id:                       1
      id-excel:                 1
      type:                     presencechainecaracteres
      message:                  message test
      zone:                     200
      priorite:                 P1
      souszone:                 a
      type-de-verification:     STRICTEMENT
      chaine-caracteres:        un texte à chercher
      autre-chaine-caracteres:
        - operateur:            OU
          chaine-caracteres:    ou autre texte à chercher

    - id:                       2
      id-excel:                 2
      type:                     presencechainecaracteres
      message:                  message test
      zone:                     200
      priorite:                 P1
      souszone:                 a
      type-de-verification:     CONTIENT
      chaine-caracteres:        premier texte à chercher
      autre-chaine-caracteres:
        - operateur:            ET
          chaine-caracteres:    et deuxième texte à chercher
        - operateur:            OU
          chaine-caracteres:    autre autre texte à chercher
```
*/








