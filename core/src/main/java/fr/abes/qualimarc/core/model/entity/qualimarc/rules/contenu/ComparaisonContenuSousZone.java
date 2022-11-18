package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.EnumTypeVerification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe qui définie une règle permettant de tester la présence d'une zone et sous-zone ainsi
 * que la comparaison entre la position ou la conformité d'une ou plusieurs chaine de caractères
 * de deux sous-zone
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="RULE_COMPARAISONCONTENUSOUSZONE")
public class ComparaisonContenuSousZone extends SimpleRule implements Serializable {

    @Column(name ="SOUS_ZONE")
    @NotNull
    private String sousZone;

    @Column(name ="ZONE_CIBLE")
    @NotNull
    private String zoneCible;

    @Column(name ="SOUS_ZONE_CIBLE")
    @NotNull
    private String sousZoneCible;

    @Column(name ="ENUM_TYPE_DE_VERIFICATION")
    @Enumerated(EnumType.STRING)
    @NotNull
    private EnumTypeVerification enumTypeVerification;

    /**
     * Constructeur sans liste de chaine de caractères avec zoneCible
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone première sous-zone pour effectuer la comparaison
     * @param sousZoneCible deuxième sous-zone pour effectuer la comparaison
     * @param enumTypeVerification type de vérficiation à appliquer pour la règle
     */
    public ComparaisonContenuSousZone(Integer id, String zone, String sousZone, String zoneCible, String sousZoneCible, EnumTypeVerification enumTypeVerification){
        super(id, zone);
        this.sousZone = sousZone;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.enumTypeVerification = enumTypeVerification;
    }

    /**
     * Méthode qui teste la présence d'une zone et sous-zone et qui compare
     * la position ou la conformité d'une ou plusieurs chaine de caractères
     * de deux sous-zone
     * @param noticeXml notice sur laquelle va être testé la règle
     * @return boolean
     */
    @Override
    public boolean isValid(NoticeXml noticeXml){

        //  Création du boolean de résultat
        boolean isComparisonValid = false;

        //  Trouver la première occurence de zoneSource
        Datafield datafieldSource = noticeXml.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).findFirst().get();

        // Trouver la valeur de la première occurence sousZoneSource de la première occurence de la zoneSource
        String sousZoneSourceValue =  datafieldSource.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).findFirst().get().getValue();

        //  Récupérer les zonesCible excepté la première occurence
        List<Datafield> zoneCibleList = noticeXml.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.zoneCible)).collect(Collectors.toList());

        //  Si la zoneSource est identique à la zoneCible et que la sousZoneSource est identique à la sousZoneCible, alors on supprime la première occurence DataField
        if(this.getZone().equals(this.zoneCible) && this.sousZone.equals(this.sousZoneCible)) {
            if (!zoneCibleList.isEmpty()) {
                zoneCibleList.remove(0);
            }
        }

        //  Pour chaque occurence de la zone cible
        for (Datafield zoneCible : zoneCibleList) {

            //  Sur toutes les occurences de sousZoneCible
            for (SubField sousZoneCible : zoneCible.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZoneCible)).collect(Collectors.toList())) {
                switch (enumTypeVerification) {
                    case STRICTEMENT:
                        isComparisonValid = sousZoneSourceValue.equals(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return isComparisonValid;
                        }
                        break;
                    case CONTIENT:
                        isComparisonValid = sousZoneSourceValue.contains(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return isComparisonValid;
                        }
                        break;
                    case NECONTIENTPAS:
                        isComparisonValid = !sousZoneSourceValue.contains(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return isComparisonValid;
                        }
                        break;
                    case COMMENCE:
                        isComparisonValid = sousZoneSourceValue.startsWith(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return isComparisonValid;
                        }
                        break;
                    case TERMINE:
                        isComparisonValid = sousZoneSourceValue.endsWith(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return isComparisonValid;
                        }
                        break;
                }
            }
        }
        return isComparisonValid;
    }

    /**
     * Méthode qui retourne la zone et la sousZone de l'objet ComparaisonContenuSousZone instancié
     * @return String
     */
    @Override
    public String getZones() {
        return this.getZone() + "$" + this.sousZone + " - " + this.zoneCible + "$" + this.sousZoneCible;
    }

}
