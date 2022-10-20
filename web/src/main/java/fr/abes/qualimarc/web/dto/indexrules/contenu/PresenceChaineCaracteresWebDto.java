package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@JsonTypeName("presencechainecaracteres")
public class PresenceChaineCaracteresWebDto extends SimpleRuleWebDto {

    @Pattern(regexp = "(\\b([a-zA-Z]{0,1})\\b)(\\b([0-9]{0,1})\\b)", message = "Le champ souszone ne peut contenir qu'une lettre (en minuscule ou majuscule), ou un chiffre.")
    @NotNull(message = "Le champ souszone est obligatoire")
    @JsonProperty("souszones")
    private String sousZones;

    @Pattern(regexp = "STRICTEMENT|CONTIENT|COMMENCE|TERMINE", message = "Le champ niveau de verification ne peut contenir que STRICTEMENT, CONTIENT, COMMENCE, TERMINE")
    @JsonProperty("niveaudeverification")
    @NotNull
    private String niveauDeVerification;

    @JsonProperty("chainecaracteres")
    @NotNull
    private String chaineCaracteres;

    @JsonProperty("autrechainecaracteres")
    @NotNull
    private List<ChaineCaracteresWebDto> listChaineCaracteres;

    //TODO construire les constructeurs en fonction de la syntaxe du yaml à déterminer

    @Getter
    @NoArgsConstructor
    public static class ChaineCaracteresWebDto {
        @Pattern(regexp = "ET|OU", message = "L'opérateur doit être égal à OU ou à ET")
        @JsonProperty("operateur")
        private String operateur;

        @JsonProperty("chainecaracteres")
        @NotNull
        private String chaineCaracteres;

        /**
         * Constructeur utilisé pour la première chaîne de caractères
         */
        public ChaineCaracteresWebDto(String chaineCaracteres){
            this.chaineCaracteres = chaineCaracteres;
        }

        public ChaineCaracteresWebDto(String operateur, String chaineCaracteres) {
            this.operateur = operateur;
            this.chaineCaracteres = chaineCaracteres;
        }
    }

}
