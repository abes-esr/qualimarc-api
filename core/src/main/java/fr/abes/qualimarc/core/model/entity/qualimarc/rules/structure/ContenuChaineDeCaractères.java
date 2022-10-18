package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RULE_CHAINECARACTERES")
public class ContenuChaineDeCaractères extends SimpleRule implements Serializable {

    @Column(name = "SOUS_ZONE")
    private String sousZone;

    @Column(name = "CHAINE_UN")
    private String chaineUn;

    @Column(name = "CHAINE_DEUX")
    private String chaineDeux;

    @Column(name= "BOOLEAN_OPERATOR")
    private BooleanOperateur booleanOperateur;

    public ContenuChaineDeCaractères(Integer id, String zone, String sousZone, String chaineUn, String chaineDeux, BooleanOperateur booleanOperateur) {
        super(id, zone);
        this.sousZone = sousZone;
        this.chaineUn = chaineUn;
        this.chaineDeux = chaineDeux;
        this.booleanOperateur = booleanOperateur;
    }

    @Override
    public boolean isValid(NoticeXml noticeXml) {
        //récupération de toutes les zones définies dans la règle
        List<Datafield> zones = noticeXml.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).collect(Collectors.toList());

        for (Datafield zone : zones) {
            // si contenu de la sousZone contient strictement (===) la chaine de caratères "chaineDeCaractères" alors lever le message (true)


            // si la sousZone est absente (isEmpty) OU si elle ne contient pas strictement (!isEmpty && !=) la chaine de caractère "chaineDeCaractère", pas de lever de message (false)
            if (zone.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.sousZone)) || ) {
                return false;
            }

            // si la sousZone contient (trouver la méthode) la chaine de caratère "chaineDeCaractère01" OU la chaine de caractère "chaineDeCaractère02", alors lever le message (true)


            // Si la sousZone est absente (isEmpty) OU si elle ne contient ni "chaineDeCaractère01" ni "chaineDeCaractère02", pas de lever de message (false)
        }


        return false;
    }

    @Override
    public String getZones() {
        return null;
    }
}
