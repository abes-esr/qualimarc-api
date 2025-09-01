package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
    private ComparaisonOperateur comparaisonOperateur;
    @NotNull
    @Column(name = "OCCURRENCES")
    private Integer occurrences;

    public NombreCaracteres(Integer id, String zone, Boolean affichageEtiquette,  @NotNull String sousZone, @NotNull ComparaisonOperateur comparaisonOperateur, @NotNull Integer occurrences) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.comparaisonOperateur = comparaisonOperateur;
        this.occurrences = occurrences;
    }

    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml notice = notices[0];
        List<Datafield> zonesSource = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).collect(Collectors.toList());
        boolean isOk = false;
        for (Datafield zone : zonesSource) {
            for (SubField subField : zone.getSubFields().stream().filter(ss -> ss.getCode().equals(this.sousZone)).collect(Collectors.toList())) {
                switch (this.comparaisonOperateur) {
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
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone + "$" + this.sousZone);
        return listZones;
    }
}
