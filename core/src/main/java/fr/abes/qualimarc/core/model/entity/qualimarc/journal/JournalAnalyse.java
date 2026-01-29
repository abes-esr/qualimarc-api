package fr.abes.qualimarc.core.model.entity.qualimarc.journal;

import fr.abes.qualimarc.core.utils.TypeAnalyse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "JOURNAL_ANALYSE")
@Getter @Setter
@NoArgsConstructor
public class JournalAnalyse implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "DATE_TIME")
    private Date dateTime;

    @Column(name = "TYPE_ANALYSE")
    @Enumerated(EnumType.STRING)
    private TypeAnalyse typeAnalyse;

    @Column(name = "NB_PPN_ANALYSE")
    private Integer nbPpnAnalyse;

    @Column(name = "NB_PPN_ERREUR")
    private Integer nbPpnErreur;

    @Column(name = "NB_PPN_OK")
    private Integer nbPpnOk;

    @Column(name = "NB_PPN_INCONNUS")
    private Integer nbPpnInconnus;

    @Column(name = "REJOUE")
    private boolean isReplayed;

    public JournalAnalyse(Date dateTime, TypeAnalyse typeAnalyse, boolean isReplayed) {
        this.dateTime = dateTime;
        this.typeAnalyse = typeAnalyse;
        this.isReplayed = isReplayed;
    }
}
