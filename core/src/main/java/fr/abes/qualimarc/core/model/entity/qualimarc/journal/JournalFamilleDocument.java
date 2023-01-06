package fr.abes.qualimarc.core.model.entity.qualimarc.journal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "JOURNAL_FAMILLE_DOC")
@Getter @Setter
@NoArgsConstructor
public class JournalFamilleDocument implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "DATE_TIME")
    private Date dateTime;

    @Column(name = "FAMILLE_DOCUMENT")
    private String familleDocument;

    public JournalFamilleDocument(Date dateTime, String familleDocument) {
        this.dateTime = dateTime;
        this.familleDocument = familleDocument;
    }
}
