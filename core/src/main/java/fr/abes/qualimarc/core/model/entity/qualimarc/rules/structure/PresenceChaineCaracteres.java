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
 * Classe qui définie une règle permettant de tester la présence d'une zone et sous-zone ainsi que la présence,
 * la position ou la conformité d'une ou plusieurs chaines de caractères dans une sous-zone.
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
     * Méthode qui vérifie la présence ou la position d'une ou plusieurs chaine.s de caractères dans une sous-zone d'une notice
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
                if (subField.getCode().equals(sousZone)) {
                    // création du boolean de résultat
                    boolean isOk;
                    // détermination du type de recherche
                    switch (enumChaineCaracteres) {
                        // Si la sous-zone contient STRICTEMENT la/les chaine.s de caractères
                        case STRICTEMENT:
                            isOk = subField.getValue().equals(chaineCaracteres);
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().equals(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            return isOk;
                        // Si la sous-zone COMMENCE par la/les chaine.s de caractères
                        case COMMENCE:
                            isOk = subField.getValue().startsWith(chaineCaracteres);
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().startsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            return isOk;
                        // Si la sous-zone TERMINE par la/les chaine.s de caractères
                        case TERMINE:
                            isOk = subField.getValue().endsWith(chaineCaracteres);
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().endsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().endsWith(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            return isOk;
                        // Si la sous-zone CONTIENT la/les chaine.s de caractères
                        case CONTIENT:
                            isOk = subField.getValue().contains(chaineCaracteres);
                            if (listChainesCaracteres != null && !listChainesCaracteres.isEmpty()) {
                                for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                                ) {
                                    // si l'opérateur logique de la chaine de caractères recherchée est ET
                                    if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.ET)) {
                                        isOk &= subField.getValue().contains(chaineCaracteres.getChaineCaracteres());
                                    }
                                    // si l'opérateur logique de la chaine de caractères recherchée est OU
                                    else if (chaineCaracteres.getBooleanOperateur().equals(BooleanOperateur.OU)) {
                                        isOk |= subField.getValue().contains(chaineCaracteres.getChaineCaracteres());
                                    }
                                }
                            }
                            return isOk;
                    }
                }
            }
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
