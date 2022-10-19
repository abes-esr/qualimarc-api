package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.EnumChaineCaracteres;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe qui définie une règle qui permet de tester la présence d'une sous-zone et la position ou la conformité d'une ou plusieurs chaines de caractères dans une sous-zone
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_PRESENCECHAINECARACTERES")
public class PresenceChaineCaracteres extends SimpleRule implements Serializable {

    @Column(name = "SOUS_ZONE")
    @NotNull
    private String sousZone;

    @OneToMany(mappedBy = "presenceChaineCaracteres", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ChaineCaracteres> listChainesCaracteres;

    public PresenceChaineCaracteres(Integer id, String zone, String sousZone, List<ChaineCaracteres> listChainesCaracteres) {
        super(id, zone);
        this.sousZone = sousZone;
        this.listChainesCaracteres = listChainesCaracteres;
    }

    /**
     * Méthode qui vérifie la présence d'une ou plusieurs chaine.s de caractères dans une sous-zone d'une notice
     * @param noticeXml notice sur laquelle va être testé la règle
     * @return boolean
     */
    @Override
    public boolean isValid(NoticeXml noticeXml) {
        //récupération de toutes les zones définies dans la règle
        List<Datafield> zones = noticeXml.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).collect(Collectors.toList());

        for (Datafield zone : zones) {

            for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                 ) {
                if (zone.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.sousZone))) {
                    // si la sousZone est absente, alors ne pas lever le message (return false)
                    return false;
                } else if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.UNIQUEMENT) && zone.getSubFields().stream().anyMatch(subField -> subField.getValue().equals(chaineCaracteres.getChaineCaracteres()))) {
                    // si contenu de la sousZone contient STRICTEMENT la/les chaine.s de caratères définie.s, alors lever le message (return true)
                    return true;
                } else if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.CONTIENT) && zone.getSubFields().stream().anyMatch(subField -> subField.getValue().contains(chaineCaracteres.getChaineCaracteres()))) {
                    // si le contenu de la souszone contient la/les chaine.s de caractères définie.s, alors lever le message (return true)
                    return true;
                } else if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.COMMENCE) && zone.getSubFields().stream().anyMatch(subField -> subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres()))) {
                    // si le contenu de la sousZone commence par la/les chaine.s de caractères définie.s, alors lever le message (return true)
                    return true;
                } else if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.TERMINE) && zone.getSubFields().stream().anyMatch(subField -> subField.getValue().endsWith(chaineCaracteres.getChaineCaracteres()))) {
                    // si le contenu de la sousZone fini par la/les chaine.s de caractères définie.s, alors lever le message (return true)
                    return true;
                } else {
                    // si aucune règle n'a pu être vérifiée, alors ne pas lever le message (return false)
                    return false;
                }
            }
        }
        // si la zone n'a été trouvée, alors ne pas lever le message (return false)
        return false;
    }

    /**
     * Méthode qui retourne la zone et la sousZone de l'objet PresenceChaineCaracteres instancié
     * @return String
     */
    @Override
    public String getZones() {
        return this.getZones() + "$" + this.getSousZone();
    }
}
