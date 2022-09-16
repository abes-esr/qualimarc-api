package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.utils.Operateur;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "RULE_NOMBREZONES")
public class NombreZone extends Rule implements Serializable {
    @Column(name = "OPERATEUR")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Operateur operateur;

    @Column(name = "OCCURRENCES")
    @NotNull
    private Integer occurrences;

    public NombreZone(Integer id, String message, String zone, Priority priority, Operateur operateur, Integer occurrences) {
        super(id, message, zone, priority);
        this.operateur = operateur;
        this.occurrences = occurrences;
    }

    public NombreZone(Integer id, String message, String zone, Priority priority, Set<FamilleDocument> famillesDocuments, Operateur operateur, Integer occurrences) {
        super(id, message, zone, priority, famillesDocuments);
        this.operateur = operateur;
        this.occurrences = occurrences;
    }

    @Override
    public boolean isValid(NoticeXml notice) {
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
}
