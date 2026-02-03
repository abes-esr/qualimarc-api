package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.TypeVerification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

    @Column(name = "ENUM_TYPE_DE_VERIFICATION")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeVerification typeDeVerification;

    @Column(name = "POSITIONSTART")
    private Integer positionStart;

    @Column(name = "POSITIONEND")
    private Integer positionEnd;

    @OneToMany(mappedBy = "presenceChaineCaracteres", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ChaineCaracteres> listChainesCaracteres;

    /**
     * Constructeur sans liste de chaines de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone sous-zone sur laquelle appliquer la recherhe
     * @param typeDeVerification type de vérification à appliquer pour la règle
     */
    public PresenceChaineCaracteres(Integer id, String zone, Boolean affichageEtiquette,  String sousZone, TypeVerification typeDeVerification) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.listChainesCaracteres = new TreeSet<>();
    }

    /**
     * Constructeur avec liste de chaines de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param affichageEtiquette false si l'étiquette de la zone ne doit pas être renvoyée au front
     * @param sousZone sous-zone sur laquelle appliquer la recherhe
     * @param typeDeVerification type de vérification à appliquer pour la règle
     * @param listChainesCaracteres liste de chaines de caractères à rechercher
     */
    public PresenceChaineCaracteres(Integer id, String zone, Boolean affichageEtiquette, String sousZone, TypeVerification typeDeVerification, TreeSet<ChaineCaracteres> listChainesCaracteres) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.listChainesCaracteres = listChainesCaracteres;
    }

    public PresenceChaineCaracteres(Integer id, String zone, Boolean affichageEtiquette, String sousZone, Integer positionStart, Integer positionEnd, TypeVerification typeDeVerification) {
        super(id, zone, affichageEtiquette);
        this.sousZone = sousZone;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
        this.typeDeVerification = typeDeVerification;
        this.listChainesCaracteres = new TreeSet<>();
    }


    /**
     * Méthode qui ajoute une chaine de caractères à la liste de chaines de caractères
     * @param chaine chaine de caractères à rechercher
     */
    public void addChaineCaracteres(ChaineCaracteres chaine) {
        this.listChainesCaracteres.add(chaine);
    }

    /**
     * Méthode qui vérifie la présence ou la position d'une ou plusieurs chaine.s de caractères dans une sous-zone d'une notice
     * @param notices notice sur laquelle va être testé la règle
     * @return boolean
     */
    @Override
    public boolean isValid(NoticeXml ... notices) {
        NoticeXml notice = notices[0];
        //récupération de toutes les zones définies dans la règle
        List<Datafield> zones = notice.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).filter(datafield -> datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(sousZone))).collect(Collectors.toList());;

        // Cas ou aucune zone n'est trouvée
        if(zones.isEmpty()) {
            return false;
        }

        // Récupération de la liste des zones qui MATCH avec la règle
        List<Datafield> zonesValid = null;
        if (positionStart == null && positionEnd == null) {
            zonesValid = zones.stream().filter(datafield -> datafield.getSubFields().stream().anyMatch(subField -> (subField.getCode().equals(sousZone)) && isValueValidWithChaineCaracteres(subField.getValue(), typeDeVerification))).collect(Collectors.toList());
        }
        if (positionStart != null && positionEnd == null) {
            zonesValid = zones.stream().filter(datafield -> datafield.getSubFields().stream().anyMatch(subField -> (subField.getCode().equals(sousZone)) && isValueValidWithChaineCaracteres(subField.getValue().substring(positionStart), typeDeVerification))).collect(Collectors.toList());
        }
        if (positionStart == null && positionEnd != null) {
            zonesValid = zones.stream().filter(datafield -> datafield.getSubFields().stream().anyMatch(subField -> (subField.getCode().equals(sousZone)) && isValueValidWithChaineCaracteres(subField.getValue().substring(0, positionEnd+1), typeDeVerification))).collect(Collectors.toList());
        }
        if (positionStart != null && positionEnd != null) {
            zonesValid = zones.stream().filter(datafield -> datafield.getSubFields().stream().anyMatch(subField -> (subField.getCode().equals(sousZone)) && isValueValidWithChaineCaracteres(subField.getValue().substring(positionStart, positionEnd+1), typeDeVerification))).collect(Collectors.toList());
        }

        if (this.getComplexRule() != null && this.getComplexRule().isMemeZone()){
            this.getComplexRule().setSavedZone(zonesValid);
            return this.getComplexRule().isSavedZoneIsNotEmpty();
        }

        return !zonesValid.isEmpty();
    }

    /**
     * Méthode qui retourne la zone et la sousZone de l'objet PresenceChaineCaracteres instancié
     * @return String
     */
    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone + "$" + this.sousZone);
        return listZones;
    }

    /**
     * Méthode qui vérifie si la valeur de la sous-zone match avec les chaines de caracteres selon le type de verification
     * @param value valeur de la sous-zone
     * @param typeDeVerification type de verification
     * @return boolean
     */
    private boolean isValueValidWithChaineCaracteres(String value, TypeVerification typeDeVerification) {
        Boolean isOk = null;
        TreeSet<ChaineCaracteres> orderedListChaine = new TreeSet<>(listChainesCaracteres);
        for(ChaineCaracteres chaineCaractere : orderedListChaine) {
            boolean isValid = chaineCaractere.isValid(value, typeDeVerification);
            if(isOk == null) {
                isOk = isValid;
            } else {
                isOk = (chaineCaractere.getBooleanOperateur() == BooleanOperateur.ET) ? (isOk && isValid) : (isOk || isValid);
            }
        }
        return isOk != null && isOk;
    }
}
