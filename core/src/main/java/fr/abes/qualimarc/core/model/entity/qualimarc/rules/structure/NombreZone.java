package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "RULE_NOMBREZONES")
public class NombreZone extends SimpleRule implements Serializable {
    @Column(name = "OPERATEUR")
    @NotNull
    @Enumerated(EnumType.STRING)
    private ComparaisonOperateur comparaisonOperateur;

    @Column(name = "OCCURRENCES")
    @NotNull
    private Integer occurrences;

    public NombreZone(Integer id, String zone, ComparaisonOperateur comparaisonOperateur, Integer occurrences) {
        super(id, zone);
        this.comparaisonOperateur = comparaisonOperateur;
        this.occurrences = occurrences;
    }


    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml notice = notices[0];
        switch (this.comparaisonOperateur) {
            case EGAL:
                return notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).count() == this.occurrences;
            case INFERIEUR:
                return notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).count() < this.occurrences;
            case SUPERIEUR:
                return notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).count() > this.occurrences;
            default:
                return false;
        }
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone);
        return listZones;
    }
}
