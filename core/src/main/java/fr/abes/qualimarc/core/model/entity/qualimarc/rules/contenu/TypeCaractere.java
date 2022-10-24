package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.TypeCaracteres;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe qui définie une règle permettant de tester le type de caractere dans une sous zone
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_TYPECARACTERE")
public class TypeCaractere extends SimpleRule implements Serializable {

    @Column(name = "SOUS_ZONE")
    @NotNull
    private String sousZone;

    @ElementCollection(targetClass = TypeCaracteres.class)
    @JoinTable(name = "TYPE_CARACTERES", joinColumns = @JoinColumn(name = "TYPE_ID"))
    @Enumerated(EnumType.STRING)
    private List<TypeCaracteres> typeCaracteres;

    public TypeCaractere(Integer id, String zone, String sousZone) {
        super(id, zone);
        this.sousZone = sousZone;
        this.typeCaracteres = new ArrayList();
    }

    public void addTypeCaractere(TypeCaracteres type){
        this.typeCaracteres.add(type);
    }

    @Override
    public boolean isValid(NoticeXml noticeXml) {
        List<Datafield> zones = noticeXml.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).collect(Collectors.toList());

        for (Datafield datafield: zones){
            List<SubField> souszones = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).collect(Collectors.toList());
            for (SubField subField: souszones){
                for (TypeCaracteres type: this.typeCaracteres){
                    boolean isOk = false;
                    switch (type) {
                        case ALPHABETIQUE:
                            isOk = subField.getValue().matches(".*[a-zA-ZÀ-ÖØ-öø-ÿ].*");
                            break;
                        case ALPHABETIQUE_MAJ:
                            isOk = subField.getValue().matches(".*[A-ZÀ-ÖØ-ß].*");
                            break;
                        case ALPHABETIQUE_MIN:
                            isOk = subField.getValue().matches(".*[a-zà-öø-ÿ].*");
                            break;
                        case NUMERIQUE:
                            isOk = subField.getValue().matches(".*[1-9].*");
                            break;
                        case SPECIAL:
                            isOk = subField.getValue().matches(".*[!-/:-@\\[-`{-~¡-¿÷×].*");
                            break;
                    }
                    if(isOk){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }
}
