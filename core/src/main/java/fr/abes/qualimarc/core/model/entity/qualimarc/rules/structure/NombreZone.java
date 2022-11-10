package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.Operateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "RULE_NOMBREZONES")
public class NombreZone extends SimpleRule implements Serializable {
    @Column(name = "OPERATEUR")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Operateur operateur;

    @Column(name = "OCCURRENCES")
    @NotNull
    private Integer occurrences;

    public NombreZone(Integer id, String zone, Operateur operateur, Integer occurrences) {
        super(id, zone);
        this.operateur = operateur;
        this.occurrences = occurrences;
    }


    @Override
    public boolean isValid(NoticeXml... notices) {
        if (notices.length > 0) {
            NoticeXml notice = Arrays.stream(notices).findFirst().get();
            switch (this.operateur) {
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
        return false;
    }

    @Override
    public String getZones() {
        return this.getZone();
    }
}
