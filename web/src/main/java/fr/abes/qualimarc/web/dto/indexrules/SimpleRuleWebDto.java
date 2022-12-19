package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.abes.qualimarc.web.dto.indexrules.contenu.*;
import fr.abes.qualimarc.web.dto.indexrules.dependance.ReciprociteWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PresenceZoneWebDto.class, name = "presencezone"),
        @JsonSubTypes.Type(value = PresenceSousZoneWebDto.class, name = "presencesouszone"),
        @JsonSubTypes.Type(value = NombreZoneWebDto.class, name = "nombrezone"),
        @JsonSubTypes.Type(value = NombreSousZoneWebDto.class, name = "nombresouszone"),
        @JsonSubTypes.Type(value = PositionSousZoneWebDto.class, name="positionsouszone"),
        @JsonSubTypes.Type(value = PresenceSousZonesMemeZoneWebDto.class, name="presencesouszonesmemezone"),
        @JsonSubTypes.Type(value = IndicateurWebDto.class, name="indicateur"),
        @JsonSubTypes.Type(value = NombreCaracteresWebDto.class, name = "nombrecaractere"),
        @JsonSubTypes.Type(value = TypeCaractereWebDto.class, name = "typecaractere"),
        @JsonSubTypes.Type(value = PresenceChaineCaracteresWebDto.class, name="presencechainecaracteres"),
        @JsonSubTypes.Type(value = DependencyWebDto.class, name = "dependance"),
        @JsonSubTypes.Type(value = ReciprociteWebDto.class, name = "reciprocite"),
        @JsonSubTypes.Type(value = ComparaisonDateWebDto.class, name = "comparaisondate"),
        @JsonSubTypes.Type(value = ReciprociteWebDto.class, name = "reciprocite"),
        @JsonSubTypes.Type(value = ComparaisonContenuSousZoneWebDto.class, name = "comparaisoncontenusouszone"),
        @JsonSubTypes.Type(value = TypeDocumentWebDto.class, name = "typedocument")
})
public abstract class SimpleRuleWebDto {
    @JsonProperty("id")
    @NotNull(message = "Le champ id est obligatoire")
    protected Integer id;

    @JsonProperty("id-excel")
    protected Integer idExcel;

    @JsonProperty("jeux-de-regles")
    protected List<Integer> ruleSetList = new ArrayList<>();

    @JsonProperty("message")
    protected String message;

    @JsonProperty("priorite")
    @Pattern(regexp = "([P]{1}[1-2]{1}){1}", message = "Le champ priorité ne peut contenir qu'une des deux valeurs : P1 ou P2")
    protected String priority;

    @JsonProperty("type-doc")
    protected List<@Pattern(regexp = "\\b([A-Z]{0,2}){0,}\\b", message = "Le champ message ne peut contenir qu'une ou deux lettre(s) majuscule(s).") String> typesDoc = new ArrayList<>();

    @JsonProperty("type-these")
    private List<@Pattern(regexp = "REPRO|SOUTENANCE", message = "Le champ type-these ne peut prendre que les valeurs SOUTENANCE ou REPRO") String> typesThese = new ArrayList<>();

    @JsonProperty("zone")
    @Pattern(regexp = "\\b([A-Z]{0,1}[0-9]{3}|4XX|5XX|6XX|7XX)\\b", message = "Le champ zone doit contenir : soit trois chiffres, soit une lettre majuscule suivie de trois chiffres, soit une zone générique (4XX, 5XX, 6XX, ou 7XX).")
    protected String zone;

    @JsonProperty("operateur-booleen")
    @Pattern(regexp = "(ET|OU)", message = "Le champ operateur ne peut contenir qu'une des deux valeurs : ET / OU")
    private String booleanOperator;

    /**
     * constructeur de règle simple
     * @param id id de la regle simple
     * @param idExcel id qui permet de faire reference dans le excel
     * @param ruleSetList list d'id qui permet de savoir si une regle appartien a un jeu de regle
     * @param message string qui sera affiché lors que la regle est vrai
     * @param zone string
     * @param priority priorité de la règle
     * @param typesDoc type de document sur lequel appliquer la règle
     */
    @JsonCreator
    public SimpleRuleWebDto(@JsonProperty("id") Integer id,
                            @JsonProperty("id-excel") Integer idExcel,
                            @JsonProperty("jeux-de-regles") List<Integer> ruleSetList,
                            @JsonProperty("message") String message,
                            @JsonProperty("zone") String zone,
                            @JsonProperty("priorite") String priority,
                            @JsonProperty("type-doc") List<String> typesDoc,
                            @JsonProperty("type-these") List<String> typesThese) {
        this.id = id;
        this.idExcel = idExcel;
        this.ruleSetList = ruleSetList;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.typesDoc = typesDoc;
        this.typesThese = typesThese;
    }

    /**
     * Constructeur de règle complexe (linked Rules)
     */
    @JsonCreator
    public SimpleRuleWebDto(@JsonProperty("id") Integer id,
                            @JsonProperty("zone") String zone,
                            @JsonProperty("operateur") String booleanOperator) {
        this.id = id;
        this.zone = zone;
        this.booleanOperator = booleanOperator;
    }

    /**
     * Constructeur de règle complexe (dependency rule
     * @param id id
     * @param zone zone
     */
    public SimpleRuleWebDto(@JsonProperty("id") Integer id,
                            @JsonProperty("zone") String zone) {
        this.id = id;
        this.zone = zone;
    }

    public SimpleRuleWebDto() {
    }
}
