package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.utils.EnumTypeVerification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name ="SOUS_ZONE_CIBLE")
    @NotNull
    private String sousZoneCible;

    @Column(name ="ENUM_TYPE_DE_VERIFICATION")
    @NotNull
    private EnumTypeVerification enumTypeVerification;

    @OneToMany(mappedBy = "ComparaisonContenuSousZone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ChaineCaracteres> listChaineCaracteres;

    /**
     * Constructeur sans liste de chaine de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone première sous-zone pour effectuer la comparaison
     * @param sousZoneCible deuxième sous-zone pour effectuer la comparaison
     * @param enumTypeVerification type de vérficiation à appliquer pour la règle
     */
    public ComparaisonContenuSousZone(Integer id, String zone, String sousZone, String sousZoneCible, EnumTypeVerification enumTypeVerification){
        super(id, zone);
        this.sousZone = sousZone;
        this.sousZoneCible = sousZoneCible;
        this.enumTypeVerification = enumTypeVerification;
        this.listChaineCaracteres = new HashSet<>();
    }

    /**
     * Constructeur avec liste de chaines de caractères
     * @param id identifiant de la règle
     * @param zone zone sur laquelle appliquer la recherche
     * @param sousZone première sous-zone pour effectuer la comparaison
     * @param sousZoneCible deuxième sous-zone pour effectuer la comparaison
     * @param enumTypeVerification type de vérficiation à appliquer pour la règle
     * @param listChaineCaracteres liste de chaines de caractères à rechercher
     */
    public ComparaisonContenuSousZone(Integer id, String zone, String sousZone, String sousZoneCible, EnumTypeVerification enumTypeVerification, Set<ChaineCaracteres> listChaineCaracteres){
        super(id, zone);
        this.sousZone = sousZone;
        this.sousZoneCible = sousZoneCible;
        this.enumTypeVerification = enumTypeVerification;
        this.listChaineCaracteres = listChaineCaracteres;
    }

    /**
     * Méthode qui ajoute une chaine de caractères à la liste de chaine de caractères
     * @param chaine chaine de caractères à rechercher
     */
    public void addChaineCaracteres(ChaineCaracteres chaine) {
        this.listChaineCaracteres.add(chaine);
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
        boolean isOk = false;

        return isOk;
    }

    /**
     * Méthode qui retourne la zone et la sousZone de l'objet ComparaisonContenuSousZone instancié
     * @return String
     */
    @Override
    public String getZones() {
        return this.getZone() + "$" + this.getSousZone();
    }

}
