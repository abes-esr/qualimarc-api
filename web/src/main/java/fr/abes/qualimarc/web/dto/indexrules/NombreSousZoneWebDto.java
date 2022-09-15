package fr.abes.qualimarc.web.dto.indexrules;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonTypeName("nombresouszone")
@NoArgsConstructor
public class NombreSousZoneWebDto extends RulesWebDto {

    @JsonProperty(value = "souszone")
    private String sousZone;

    @JsonProperty(value = "zonecible")
    private String zoneCible;

    @JsonProperty(value = "souszonecible")
    private String sousZoneCible;

    public NombreSousZoneWebDto(Integer id, Integer idExcel, String message, String zone, Priority priority, List<String> typesDoc, String sousZone, String zoneCible, String sousZoneCible) {
        super(id, idExcel, message, zone, priority, typesDoc);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }
}
