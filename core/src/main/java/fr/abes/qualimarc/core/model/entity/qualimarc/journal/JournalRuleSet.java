package fr.abes.qualimarc.core.model.entity.qualimarc.journal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "JOURNAL_RULESET")
@Getter @Setter
@NoArgsConstructor
public class JournalRuleSet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "DATE_TIME")
    private Date dateTime;
    @Column(name = "RULESET_ID")
    private Integer ruleSetId;

    public JournalRuleSet(Date dateTime, Integer ruleSetId) {
        this.dateTime = dateTime;
        this.ruleSetId = ruleSetId;
    }
}
