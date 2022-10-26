package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.*;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.Indicateur;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.TypeCaractere;
import fr.abes.qualimarc.web.dto.indexrules.contenu.IndicateurWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.NombreCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.TypeCaractereWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.PresenceChaineCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
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
        @JsonSubTypes.Type(value = PresenceChaineCaracteresWebDto.class, name="presencechainecaracteres")
})
public abstract class SimpleRuleWebDto {


    @JsonProperty("id")
    @NotNull(message = "Le champ id est obligatoire")
    protected Integer id;

    @JsonProperty("id-excel")
    protected Integer idExcel;

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
    @NotNull(message = "Le champ zone est obligatoire")
    @NotBlank(message = "Le champ zone est obligatoire")
    protected String zone;

    @JsonProperty("operateur-booleen")
    @Pattern(regexp = "(ET|OU)", message = "Le champ operateur ne peut contenir qu'une des deux valeurs : ET / OU")
    private String booleanOperator;

    /**
     * constructeur de règle simple
     * @param id
     * @param idExcel
     * @param message
     * @param zone
     * @param priority
     * @param typesDoc
     */
    @JsonCreator
    public SimpleRuleWebDto(@JsonProperty("id") Integer id,
                            @JsonProperty("id-excel") Integer idExcel,
                            @JsonProperty("message") String message,
                            @JsonProperty("zone") String zone,
                            @JsonProperty("priorite") String priority,
                            @JsonProperty("type-doc") List<String> typesDoc,
                            @JsonProperty("type-these") List<String> typesThese) {
        this.id = id;
        this.idExcel = idExcel;
        this.message = message;
        this.zone = zone;
        this.priority = priority;
        this.typesDoc = typesDoc;
        this.typesThese = typesThese;
    }

    /**
     * Constructeur de règle complexe
     */
    @JsonCreator
    public SimpleRuleWebDto(@JsonProperty("id") Integer id,
                            @JsonProperty("zone") String zone,
                            @JsonProperty("operateur") String booleanOperator) {
        this.id = id;
        this.zone = zone;
        this.booleanOperator = booleanOperator;
    }

    public SimpleRuleWebDto() {
    }
}
