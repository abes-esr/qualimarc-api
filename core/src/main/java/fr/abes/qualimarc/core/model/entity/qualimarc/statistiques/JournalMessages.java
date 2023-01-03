package fr.abes.qualimarc.core.model.entity.qualimarc.statistiques;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "JOURNAL_MESSAGES")
@Getter @Setter
@NoArgsConstructor
public class JournalMessages implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "DATE_TIME")
    private Date dateTime;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "ZONES")
    private String zones;

    public JournalMessages(Date dateTime, String message, String zones) {
        this.dateTime = dateTime;
        this.message = message;
        this.zones = zones;
    }
}
