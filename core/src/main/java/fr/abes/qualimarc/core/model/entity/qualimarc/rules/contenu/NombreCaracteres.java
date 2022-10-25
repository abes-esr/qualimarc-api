package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.Operateur;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_NOMBRECARACTERES")
public class NombreCaracteres extends SimpleRule {
    @NotNull
    @Column(name = "SOUS_ZONE")
    private String sousZone;
    @NotNull
    @Column(name = "OPERATEUR")
    @Enumerated(EnumType.STRING)
    private Operateur operateur;
    @NotNull
    @Column(name = "OCCURRENCES")
    private Integer occurrences;

    public NombreCaracteres(Integer id, String zone, @NotNull String sousZone, @NotNull Operateur operateur, @NotNull Integer occurrences) {
        super(id, zone);
        this.sousZone = sousZone;
        this.operateur = operateur;
        this.occurrences = occurrences;
    }

    @Override
    public boolean isValid(NoticeXml notice) {
        List<Datafield> zonesSource = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).collect(Collectors.toList());
        boolean isOk = false;
        for (Datafield zone : zonesSource) {
            for (SubField subField : zone.getSubFields().stream().filter(ss -> ss.getCode().equals(this.sousZone)).collect(Collectors.toList())) {
                switch (this.operateur) {
                    case EGAL:
                        isOk = subField.getValue().length() == this.occurrences;
                        break;
                    case SUPERIEUR:
                        isOk = subField.getValue().length()>this.occurrences;
                        break;
                    case INFERIEUR:
                        isOk = subField.getValue().length()<this.occurrences;
                        break;
                    case INFERIEUR_EGAL:
                        isOk = subField.getValue().length()<=this.occurrences;
                        break;
                    case SUPERIEUR_EGAL:
                        isOk = subField.getValue().length()>=this.occurrences;
                        break;
                }
                if (isOk) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }
}
