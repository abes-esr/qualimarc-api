package fr.abes.qualimarc.core.model.entity.qualimarc.reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "FAMILLEDOCUMENT")
public class FamilleDocument implements Serializable {
    @Id
    @Column(name = "FAMILLEDOCUMENT_ID")
    private String id;

    @Column(name = "LIBELLE")
    private String libelle;

    @ManyToMany(mappedBy = "famillesDocuments", targetEntity = ComplexRule.class)
    @JsonIgnore
    private Set<ComplexRule> rules;

    public FamilleDocument(String id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public FamilleDocument(String id) {
        this.id = id;
    }
}
