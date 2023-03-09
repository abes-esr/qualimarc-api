package fr.abes.qualimarc.core.model.entity.qualimarc.journal;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "JOURNAL_MESSAGES")
@Getter @Setter
public class JournalMessages implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ANNEE")
    private Integer annee;

    @Column(name = "MOIS")
    private Integer mois;

    @Column(name = "MESSAGE", length = 2000)
    private String message;

    @Column(name = "OCCURRENCES")
    private Integer occurrences;

    public JournalMessages() {
        this.occurrences = 0;
    }


    public JournalMessages(String message) {
        Calendar today = Calendar.getInstance();
        this.annee = today.get(Calendar.YEAR);
        this.mois = today.get(Calendar.MONTH) + 1;
        this.message = message;
        this.occurrences = 1;
    }

    public JournalMessages(Integer annee, Integer mois, String message, Integer occurrences) {
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
