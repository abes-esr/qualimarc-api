package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.EnumChaineCaracteres;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
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

    @Column(name = "ENUM_CHAINE_CARACTERES")
    @NotNull
    private EnumChaineCaracteres enumChaineCaracteres;

    @Column(name = "CHAINE_CARACTERES")
    @NotNull
    private String chaineCaracteres;

    @OneToMany(mappedBy = "presenceChaineCaracteres", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ChaineCaracteres> listChainesCaracteres;

    public PresenceChaineCaracteres(Integer id, String zone, String sousZone, EnumChaineCaracteres enumChaineCaracteres, String chaineCaracteres) {
        super(id, zone);
        this.sousZone = sousZone;
        this.enumChaineCaracteres = enumChaineCaracteres;
        this.chaineCaracteres = chaineCaracteres;
    }

    public PresenceChaineCaracteres(Integer id, String zone, String sousZone, EnumChaineCaracteres enumChaineCaracteres, String chaineCaracteres, List<ChaineCaracteres> listChainesCaracteres) {
        super(id, zone);
        this.sousZone = sousZone;
        this.enumChaineCaracteres = enumChaineCaracteres;
        this.chaineCaracteres = chaineCaracteres;
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

        // pour chaque occurence de la zone
        for (Datafield zone : zones) {

            // pour chaque occurence de la sous-zone
            for (SubField subField : zone.getSubFields()
                 ) {
                // si la sous-zone est celle recherchée
                if (subField.getCode().equals(this.sousZone)) {
                    // création de la liste des résultats
                    boolean isOk = false;
                    // si la recherce est de type STRICTEMENT

                    switch (this.enumChaineCaracteres) {
                        case STRICTEMENT:
                            isOk &= (subField.getValue().equals(chaineCaracteres));
                            for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                            ) {
                                isOk |= subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
                            }
                            return isOk;
                        case COMMENCE:

                        case TERMINE:

                        case CONTIENT:
                    }

//                    if (this.enumChaineCaracteres.equals(EnumChaineCaracteres.STRICTEMENT)) {
//                        // toutes les chaines de caractères
//                        for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
//                             ) {
//                            if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
//                                return subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
////                                isOk &= (subField.getValue().equals(chaineCaracteres.getChaineCaracteres()));
//                            } else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
//                                isOk |= subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
//                            }
//                            return isOk;
//                        }
//                        return true;
//
//                    } else if (enumChaineCaracteres.equals(EnumChaineCaracteres.COMMENCE)) {
//
//                    } else if (enumChaineCaracteres.equals(EnumChaineCaracteres.TERMINE)) {
//
//                    }
                }
                return false;
            }


//            // teste la présence de la sous-zone
//            if (zone.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.sousZone))) {
//                //  pour chaque chaine de caractères
//                List<Boolean> isOk = Collections.singletonList(false);
//                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
//                ) {
//                    if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.STRICTEMENT)) {
//                        //  si l'une des occurences de la sous-zone contient UNIQUEMENT la chaine de caractères
//                        return zone.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).anyMatch(subField -> subField.getValue().equals(chaineCaracteres.getChaineCaracteres()));
//                    }
//                    if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.CONTIENT)) {
//                        //  si l'une des occurences de la sous-zone CONTIENT la chaine de caractères
//                            return zone.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).anyMatch(subField -> subField.getValue().contains(chaineCaracteres.getChaineCaracteres()));
//                    }
//                    if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.COMMENCE)) {
//                        // si l'une des occurences de la sous-zone COMMENCE par la chaine de caractères, alors return true
//                        if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
//
//                            isOk.add(zone.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).anyMatch(subField -> subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres())));
//
//                        } else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
//                            zone.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).anyMatch(subField -> subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres()));
//                        }
//                        return zone.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).anyMatch(subField -> subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres()));
//                    }
//                    if (chaineCaracteres.getEnumChaineCaracteres().equals(EnumChaineCaracteres.TERMINE)) {
//                        // si le contenu de la sousZone fini par la chaine de caractères, alors return true
//                        return zone.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).anyMatch(subField -> subField.getValue().endsWith(chaineCaracteres.getChaineCaracteres()));
//                    }
//                }
//                if (isOk.stream().allMatch(aBoolean -> aBoolean.equals(Boolean.TRUE))) {
//                    return true;
//                }
//                // si la sous-zone n'est pas présente, alors ne pas lever le message (return false)
//            }
//            else return false;
        }
        // si la zone n'a été trouvée, alors return false
        return false;
    }

    /**
     * Méthode qui retourne la zone et la sousZone de l'objet PresenceChaineCaracteres instancié
     * @return String
     */
    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }
}
