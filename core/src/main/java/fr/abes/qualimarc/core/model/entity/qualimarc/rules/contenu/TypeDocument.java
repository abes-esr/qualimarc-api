package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Controlfield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.TypeVerification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_TYPEDOCUMENT")
public class TypeDocument extends SimpleRule implements Serializable {
    @Column(name = "ENUM_TYPE_DE_VERIFICATION")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeVerification typeDeVerification;

    @Column(name = "POSITION")
    @NotNull
    private Integer position;

    @Column(name = "VALEUR")
    @NotNull
    private String valeur;

    public TypeDocument(Integer id, Boolean affichageEtiquette, @NotNull TypeVerification typeDeVerification, @NotNull Integer position, @NotNull String valeur) {
        super(id, "008", affichageEtiquette);
        this.typeDeVerification = typeDeVerification;
        this.position = position;
        this.valeur = valeur;
    }

    @Override
    public boolean isValid(NoticeXml... notices) {
        NoticeXml notice = notices[0];
        //récupération de toutes les zones définies dans la règle
        List<Controlfield> zones = notice.getControlfields().stream().filter(controlfield -> controlfield.getTag().equals(this.getZone())).collect(Collectors.toList());

        for (Controlfield zone : zones) {
            if (zone.getValue().length() < position)
                return true;
            switch (this.typeDeVerification) {
                case STRICTEMENT:
                    return zone.getValue().substring(this.position - 1, this.position).equalsIgnoreCase(this.valeur);
                case STRICTEMENTDIFFERENT:
                    return !zone.getValue().substring(this.position - 1, this.position).equalsIgnoreCase(this.valeur);
                default:
                    throw new IllegalArgumentException("Opérateur non autorisé sur cette règle");
            }
        }
        return false;
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone);
        return listZones;
    }
}
