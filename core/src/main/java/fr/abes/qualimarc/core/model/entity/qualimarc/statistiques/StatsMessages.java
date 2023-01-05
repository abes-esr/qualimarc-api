package fr.abes.qualimarc.core.model.entity.qualimarc.statistiques;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "STAT_MESSAGES")
@Getter @Setter
public class StatsMessages implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ANNEE")
    private Integer annee;

    @Column(name = "MOIS")
    private Integer mois;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "OCCURRENCES")
    private Integer occurrences;

    public StatsMessages() {
        this.occurrences = 0;
    }


    public StatsMessages(String message) {
        Calendar today = Calendar.getInstance();
        this.annee = today.get(Calendar.YEAR);
        this.mois = today.get(Calendar.MONTH) + 1;
        this.message = message;
        this.occurrences = 1;
    }

    public StatsMessages(Integer annee, Integer mois, String message, Integer occurrences) {
        this.annee = annee;
        this.mois = mois;
        this.message = message;
        this.occurrences = occurrences;
    }

    public void addOccurrence() {
        this.occurrences++;
    }

    public void addOccurrence(Integer occurrences) {
        this.occurrences += occurrences;
    }
}
