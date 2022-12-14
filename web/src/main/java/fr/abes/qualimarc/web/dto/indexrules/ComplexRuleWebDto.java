package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.LinkedList;
import java.util.List;

@Data
public class ComplexRuleWebDto {
    @JsonProperty("id")
    @NotNull(message = "Le champ id est obligatoire")
    private Integer id;

    @JsonProperty("id-excel")
    private Integer idExcel;

    @Pattern(regexp = "\\b([A-Z]{0,1}[0-9]{3}|4XX|5XX|6XX|7XX)\\b", message = "Le champ zone doit contenir : soit trois chiffres, soit une lettre majuscule suivie de trois chiffres, soit une zone générique (4XX, 5XX, 6XX, ou 7XX).")
    @JsonProperty("zone")
    private String zone;

    @JsonProperty("jeux-de-regles")
    private List<Integer> ruleSetList;

    @JsonProperty("message")
    @NotNull(message = "Le champ message est obligatoire")
    @NotBlank(message = "Le champ message est obligatoire")
    private String message;

    @JsonProperty("priorite")
    @Pattern(regexp = "([P]{1}[1-2]{1}){1}", message = "Le champ priorité ne peut contenir qu'une des deux valeurs : P1 ou P2")
    @NotNull(message = "Le champ priorite est obligatoire")
    @NotBlank(message = "Le champ priorite est obligatoire")
    private String priority;

    @JsonProperty("type-doc")
    private List<@Pattern(regexp = "\\b([A-Z]{0,2}){0,}\\b", message = "Le champ type-doc ne peut contenir qu'une ou deux lettre(s) majuscule(s).") String> typesDoc;

    @JsonProperty("type-these")
    private List<@Pattern(regexp = "\\bREPRO|SOUTENANCE\\b", message = "Le champ type-these ne peut prendre que les valeurs SOUTENANCE ou REPRO") String> typesThese;

    @JsonProperty("regles")
    @NotEmpty(message = "Une règle complexe doit contenir au moins une règle")
    private List<SimpleRuleWebDto> regles;

    public void addRegle(SimpleRuleWebDto rule) {
        this.regles.add(rule);
    }

    public ComplexRuleWebDto() {
        this.regles = new LinkedList<>();
    }
}
