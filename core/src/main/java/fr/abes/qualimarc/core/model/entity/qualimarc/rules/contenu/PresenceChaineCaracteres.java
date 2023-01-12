package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.TypeVerification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
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

    @OneToMany(mappedBy = "presenceChaineCaracteres", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ChaineCaracteres> listChainesCaracteres;

    /**
     * Constructeur sans liste de chaines de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone sous-zone sur laquelle appliquer la recherhe
     * @param typeDeVerification type de vérification à appliquer pour la règle
     */
    public PresenceChaineCaracteres(Integer id, String zone, String sousZone, TypeVerification typeDeVerification) {
        super(id, zone);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.listChainesCaracteres = new HashSet<>();
    }

    /**
     * Constructeur avec liste de chaines de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone sous-zone sur laquelle appliquer la recherhe
     * @param typeDeVerification type de vérification à appliquer pour la règle
     * @param listChainesCaracteres liste de chaines de caractères à rechercher
     */
    public PresenceChaineCaracteres(Integer id, String zone, String sousZone, TypeVerification typeDeVerification, Set<ChaineCaracteres> listChainesCaracteres) {
        super(id, zone);
        this.sousZone = sousZone;
        this.typeDeVerification = typeDeVerification;
        this.listChainesCaracteres = listChainesCaracteres;
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

        //  Tri la liste de chaineCaracteres
        sortListChaineCaracteres();

        // Récupération de la liste des zones qui MATCH avec la règle
        List<Datafield> zonesValid = zones.stream().filter(datafield -> datafield.getSubFields().stream().anyMatch(subField -> (subField.getCode().equals(sousZone)) && isValueValidWithChaineCaracteres(subField.getValue(), typeDeVerification))).collect(Collectors.toList());

        if (this.getComplexRule() != null && this.getComplexRule().isMemeZone()){
            this.getComplexRule().setSavedZone(zonesValid);
            return this.getComplexRule().isSavedZoneIsNotEmpty();
        }

        return zonesValid.size() > 0;
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

    private void sortListChaineCaracteres() {
        listChainesCaracteres = listChainesCaracteres.stream().sorted(Comparator.comparing(ChaineCaracteres::getPosition)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Méthode qui vérifie si la valeur de la sous-zone match avec les chaines de caracteres selon le type de verification
     * @param value valeur de la sous-zone
     * @param typeDeVerification type de verification
     * @return boolean
     */
    private boolean isValueValidWithChaineCaracteres(String value, TypeVerification typeDeVerification) {
        boolean isOk = false;
        for( ChaineCaracteres chaineCaractere : listChainesCaracteres) {
            if(chaineCaractere.getBooleanOperateur() == BooleanOperateur.ET) {
                isOk &= chaineCaractere.isValid(value, typeDeVerification);
            } else {
                isOk |= chaineCaractere.isValid(value, typeDeVerification);
            }
        }
        return isOk;
    }
}
