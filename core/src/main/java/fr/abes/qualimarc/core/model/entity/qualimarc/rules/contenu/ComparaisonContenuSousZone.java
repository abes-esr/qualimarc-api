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
import java.util.ArrayList;
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

    @Column(name ="POSITION_START")
    private Integer positionStart;

    @Column(name ="POSITION_END")
    private Integer positionEnd;

    @Column(name ="POSITION")
    private Integer position;

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

    @Column(name ="POSITION_START_CIBLE")
    private Integer positionStartCible;

    @Column(name ="POSITION_END_CIBLE")
    private Integer positionEndCible;

    @Column(name ="POSITION_CIBLE")
    private Integer positionCible;

    /**
     * Constructeur sans liste de chaine de caractères avec zoneCible
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone première sous-zone pour effectuer la comparaison
     * @param sousZoneCible deuxième sous-zone pour effectuer la comparaison
     * @param typeVerification type de vérficiation à appliquer pour la règle
     */
    public ComparaisonContenuSousZone(Integer id, String zone, Boolean affichageEtiquette,  String sousZone, TypeVerification typeVerification, String zoneCible, String sousZoneCible){
        super(id, zone, affichageEtiquette);
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
    public ComparaisonContenuSousZone(Integer id, String zone, Boolean affichageEtiquette,  String sousZone, TypeVerification typeVerification, Integer nombreCaracteres, String zoneCible, String sousZoneCible){
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.typeVerification = typeVerification;
        this.nombreCaracteres = nombreCaracteres;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    public ComparaisonContenuSousZone(Integer id, String zone, Boolean affichageEtiquette, String sousZone, Integer positionStart, Integer positionEnd, Integer position, TypeVerification typeVerification, Integer nombreCaracteres, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible, Integer positionCible) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.position = position;
        this.typeVerification = typeVerification;
        this.nombreCaracteres = nombreCaracteres;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
        this.positionCible = positionCible;
    }

    public ComparaisonContenuSousZone(Integer id, String zone, Boolean affichageEtiquette, String sousZone, Integer positionStart, Integer positionEnd, TypeVerification typeVerification, String zoneCible, String sousZoneCible, Integer positionStartCible, Integer positionEndCible) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.typeVerification = typeVerification;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionStartCible = positionStartCible;
        this.positionEndCible = positionEndCible;
    }

    public ComparaisonContenuSousZone(Integer id, String zone, Boolean affichageEtiquette, String sousZone, Integer position, TypeVerification typeVerification, String zoneCible, String sousZoneCible, Integer positionCible) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.position = position;
        this.typeVerification = typeVerification;
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
        this.positionCible = positionCible;
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
            if(positionStart != null && positionEnd != null){
                sousZoneSourceValue = subFieldSource.getValue().substring(positionStart, positionEnd+1);
            } else if (positionStart != null){
                sousZoneSourceValue = subFieldSource.getValue().substring(positionStart);
            } else if (positionEnd != null){
                sousZoneSourceValue = subFieldSource.getValue().substring(0,positionEnd+1);
            } else if (position != null){
                sousZoneSourceValue = subFieldSource.getValue().substring(position,position+1);
            } else {
                sousZoneSourceValue = subFieldSource.getValue();
            }
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
                if(positionStartCible != null && positionEndCible != null){
                    caractereSearch = caractereSearch.substring(positionStartCible, positionEndCible+1);
                } else if (positionStartCible != null){
                    caractereSearch = caractereSearch.substring(positionStartCible);
                } else if (positionEndCible != null){
                    caractereSearch = caractereSearch.substring(0,positionEndCible+1);
                } else if (positionCible != null){
                    caractereSearch = caractereSearch.substring(positionCible,positionCible+1);
                }

                switch (typeVerification) {
                    case STRICTEMENT:
                        isComparisonValid = sousZoneSourceValue.equals(caractereSearch);
                        break;
                    case STRICTEMENTDIFFERENT:
                        isComparisonValid = !sousZoneSourceValue.equals(caractereSearch);
                        break;
                    case CONTIENT:
                        isComparisonValid = sousZoneSourceValue.contains(caractereSearch);
                        break;
                    case NECONTIENTPAS:
                        isComparisonValid = !sousZoneSourceValue.contains(caractereSearch);
                        break;
                    case COMMENCE:
                        if(nombreCaracteres != null && nombreCaracteres !=0){
                            caractereSearch = sousZoneCible.getValue().substring(0, nombreCaracteres);
                        }
                        isComparisonValid = sousZoneSourceValue.startsWith(caractereSearch);
                        break;
                    case TERMINE:
                        if(nombreCaracteres != null && nombreCaracteres !=0){
                            caractereSearch = sousZoneCible.getValue().substring((sousZoneCible.getValue().length() - nombreCaracteres));
                        }
                        isComparisonValid = sousZoneSourceValue.endsWith(caractereSearch);
                        break;
                }
                if (isComparisonValid) {
                    return true;
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
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone + "$" + this.sousZone);
        listZones.add(this.zoneCible + "$" + this.sousZoneCible);
        return listZones;
    }

}
