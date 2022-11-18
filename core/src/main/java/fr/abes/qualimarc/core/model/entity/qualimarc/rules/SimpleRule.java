package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Une règle simple n'est jamais utilisée seule, il s'agit d'un objet abstrait qui peut prendre plusieurs type (en fonction du type de l'objet enfant)
 * Elle est toujours rattachée à une règle complexe
 */
@Entity
@NoArgsConstructor
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "SIMPLE_RULE")
public abstract class SimpleRule {
    @Id
    @Column(name = "ID")
    protected Integer id;
    @Column(name = "ZONE")
    protected String zone;
    @OneToOne(mappedBy = "firstRule")
    private ComplexRule complexRule;
    @OneToOne(mappedBy = "rule")
    private LinkedRule linkedRule;

    public SimpleRule(Integer id, String zone) {
        this.id = id;
        this.zone = zone;
    }

    public abstract boolean isValid(NoticeXml ... notices);

    public abstract String getZones();

}
