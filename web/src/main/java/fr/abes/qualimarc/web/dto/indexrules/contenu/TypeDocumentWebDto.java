package fr.abes.qualimarc.web.dto.indexrules.contenu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@JsonTypeName("typedocument")
@NoArgsConstructor
public class TypeDocumentWebDto extends SimpleRuleWebDto {
    @Pattern(regexp = "STRICTEMENT|STRICTEMENTDIFFERENT", message = "Le champ niveau de verification ne peut contenir que STRICTEMENT, CONTIENT, COMMENCE, TERMINE, NECONTIENTPAS")
    @JsonProperty("type-de-verification")
    @NotNull
    private String typeDeVerification;

    @JsonProperty("position")
    @NotNull
    private Integer position;

    @JsonProperty("valeur")
    @NotNull
    private String valeur;

    /**
     * Constructeur typeDocument
     * @param id identifiant de la règle
     * @param idExcel identifiant excel de la règle
     * @param message message à renvoyer si la règle est vérifiée
     * @param priority priorité de la règle
     * @param typesDoc type de document de la notice sur laquelle appliquer la règle
     * @param typeDeVerifications type de vérification à appliquer pour la règle
     * @param position position sur laquelle chercher dans la zone
     * @param valeur valeur à chercher dans la zone
     */
    public TypeDocumentWebDto(Integer id, Integer idExcel, List<Integer> ruleSetList, String message, boolean affichageEtiquette, String priority, List<String> typesDoc, List<String> typesThese, String typeDeVerifications, Integer position, String valeur) {
        super(id, idExcel, ruleSetList, message, affichageEtiquette, "008", priority, typesDoc, typesThese);
        this.position = position;
        this.typeDeVerification = typeDeVerifications;
        this.valeur = valeur;
    }
}
