package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.TypeVerification;
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

    @Column(name ="ENUM_TYPE_DE_VERIFICATION")
    @Enumerated(EnumType.STRING)
    @NotNull
    private TypeVerification typeVerification;

    @Column(name ="NOMBRE_CARACTERE")
    private Integer nombreCaracteres;

    @Column(name ="ZONE_CIBLE")
    @NotNull
    private String zoneCible;

    @Column(name ="SOUS_ZONE_CIBLE")
    @NotNull
    private String sousZoneCible;

    /**
     * Constructeur sans liste de chaine de caractères avec zoneCible
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone première sous-zone pour effectuer la comparaison
     * @param sousZoneCible deuxième sous-zone pour effectuer la comparaison
     * @param typeVerification type de vérficiation à appliquer pour la règle
     */
    public ComparaisonContenuSousZone(Integer id, String zone, String sousZone, TypeVerification typeVerification, String zoneCible, String sousZoneCible){
        super(id, zone);
        this.sousZone = sousZone;
        this.typeVerification = typeVerification;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    /**
     * Constructeur sans liste de chaine de caractères avec zoneCible
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone première sous-zone pour effectuer la comparaison
     * @param sousZoneCible deuxième sous-zone pour effectuer la comparaison
     * @param nombreCaracteres nombre de caractères à contrôler
     */
    public ComparaisonContenuSousZone(Integer id, String zone, String sousZone, TypeVerification typeVerification, Integer nombreCaracteres, String zoneCible, String sousZoneCible){
        super(id, zone);
        this.sousZone = sousZone;
        this.typeVerification = typeVerification;
        this.nombreCaracteres = nombreCaracteres;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    /**
     * Méthode qui teste la présence d'une zone et sous-zone et qui compare
     * la position ou la conformité d'une ou plusieurs chaine de caractères
     * de deux sous-zone
     * @param notices notice sur laquelle va être testé la règle
     * @return boolean
     */
    @Override
    public boolean isValid(NoticeXml ... notices){
        //  Sélection de la première notice de la liste de notices
        NoticeXml notice = notices[0];

        //  Création du boolean de résultat
        boolean isComparisonValid = false;

        //  Trouver la première occurence de zoneSource
        Datafield datafieldSource = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).findFirst().orElse(null);
        if( datafieldSource == null ){
            return false;
        }

        // Trouver la valeur de la première occurence sousZoneSource de la première occurence de la zoneSource
        String sousZoneSourceValue;
        if(datafieldSource.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.sousZone))){
            return false;
        }else {
            SubField subFieldSource = datafieldSource.getSubFields().stream().filter(subField -> subField.getCode().equals(this.sousZone)).findFirst().orElse(null);
            if(subFieldSource == null){
                return false;
            }
            sousZoneSourceValue = subFieldSource.getValue();
        }


        //  Récupérer les zonesCible excepté la première occurence
        List<Datafield> zoneCibleList = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.zoneCible)).collect(Collectors.toList());

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
                String caractereSearch = sousZoneCible.getValue();
                switch (typeVerification) {
                    case STRICTEMENT:
                        isComparisonValid = sousZoneSourceValue.equals(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return true;
                        }
                        break;
                    case STRICTEMENTDIFFERENT:
                        isComparisonValid = !sousZoneSourceValue.equals(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return true;
                        }
                        break;
                    case CONTIENT:
                        isComparisonValid = sousZoneSourceValue.contains(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return true;
                        }
                        break;
                    case NECONTIENTPAS:
                        isComparisonValid = !sousZoneSourceValue.contains(sousZoneCible.getValue());
                        if (isComparisonValid) {
                            return true;
                        }
                        break;
                    case COMMENCE:
                        if(nombreCaracteres != null && nombreCaracteres !=0){
                            caractereSearch = sousZoneCible.getValue().substring(0, nombreCaracteres);
                        }
                        isComparisonValid = sousZoneSourceValue.startsWith(caractereSearch);
                        if (isComparisonValid) {
                            return true;
                        }
                        break;
                    case TERMINE:
                        if(nombreCaracteres != null && nombreCaracteres !=0){
                            caractereSearch = sousZoneCible.getValue().substring((sousZoneCible.getValue().length() - nombreCaracteres), sousZoneCible.getValue().length());
                        }
                        isComparisonValid = sousZoneSourceValue.endsWith(caractereSearch);
                        if (isComparisonValid) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    /**
     * Méthode qui retourne la zone et la sousZone de l'objet ComparaisonContenuSousZone instancié
     * @return String
     */
    @Override
    public String getZones() {
        return this.getZone() + "$" + this.sousZone + " / " + this.zoneCible + "$" + this.sousZoneCible;
    }

}
