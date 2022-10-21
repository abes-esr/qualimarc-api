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

    @JsonProperty("chaine-caracteres")
    @NotNull
    private String chaineCaracteres;

    @JsonProperty("autre-chaine-caracteres")
    private List<ChaineCaracteresWebDto> listChaineCaracteres;

    public PresenceChaineCaracteresWebDto(Integer id, Integer idExcel, String message, String zone, String priority, List<String> typesDoc, String sousZone, String typeDeVerification, String chaineCaracteres) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.chaineCaracteres = chaineCaracteres;
        this.listChaineCaracteres = new LinkedList<>();
    }

    public PresenceChaineCaracteresWebDto(Integer id, String zone, String booleanOperator, String sousZone, String typeDeVerification, String chaineCaracteres, List<ChaineCaracteresWebDto> listChaineCaracteres) {
        super(id, zone, booleanOperator);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.chaineCaracteres = chaineCaracteres;
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
        @NotNull
        private String operateur;

        @JsonProperty("chaine-caracteres")
        @NotNull
        private String chaineCaracteres;

        public ChaineCaracteresWebDto(String operateur, String chaineCaracteres) {
            this.operateur = operateur;
            this.chaineCaracteres = chaineCaracteres;
        }
    }

}
