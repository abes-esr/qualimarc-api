package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "SIMPLE_RULE")
@NoArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SimpleRule {
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "ZONE")
    private String zone;
    @OneToOne(mappedBy = "firstRule")
    private ComplexRule complexRule;
    @OneToOne(mappedBy = "rule")
    private LinkedRule linkedRule;

    public SimpleRule(Integer id, String zone) {
        this.id = id;
        this.zone = zone;
    }

    public abstract boolean isValid(NoticeXml noticeXml);

    public abstract String getZones();
}