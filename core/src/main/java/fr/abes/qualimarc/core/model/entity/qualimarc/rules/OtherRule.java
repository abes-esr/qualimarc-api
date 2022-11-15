package fr.abes.qualimarc.core.model.entity.qualimarc.rules;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "LINKED_RULE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
@NoArgsConstructor
public abstract class OtherRule implements Serializable {
    @Id
    @Column(name = "ID_LINKED_RULE")
    private Integer id;

    @Column(name = "POSITION")
    private Integer position;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_COMPLEX_RULE")
    private ComplexRule complexRule;

    public OtherRule(Integer id, Integer position, ComplexRule complexRule) {
        this.id = id;
        this.position = position;
        this.complexRule = complexRule;
    }

    public abstract String getZones();
}
