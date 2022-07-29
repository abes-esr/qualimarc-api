package fr.abes.qualimarc.core.model.entity.qualimarc.reference;

import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "FAMILLEDOCUMENT")
public class FamilleDocument {
    @Id
    @Column(name = "FAMILLEDOCUMENT_ID")
    private String id;

    @Column(name = "LIBELLE")
    private String libelle;

    @ManyToMany(mappedBy = "famillesDocuments", targetEntity = Rule.class)
    private Set<Rule> rules;

    public FamilleDocument(String id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }
}
