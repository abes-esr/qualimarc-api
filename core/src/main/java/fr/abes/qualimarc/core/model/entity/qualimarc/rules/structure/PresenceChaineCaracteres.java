package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.chainecaracteres.ChaineCaracteres;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public boolean isValid(NoticeXml noticeXml) {
        //récupération de toutes les zones définies dans la règle
        List<Datafield> zones = noticeXml.getDatafields().stream().filter(datafield -> datafield.getTag().equals(this.getZone())).collect(Collectors.toList());

        for (Datafield zone : zones) {
            // si contenu de la sousZone contient strictement la/les chaines de caratères défini, alors lever le message (return true)
            for (ChaineCaracteres chaineCaracteres : listChainesCaracteres
                 ) {
                if (zone.getSubFields().stream().anyMatch(subField -> subField.getValue().equals(chaineCaracteres.getChaineCaracteres()))) {
                    return true;
                }
            }


            // si la sousZone est absente (isEmpty) OU si elle ne contient pas strictement (!isEmpty && !=) la chaine de caractère "Complément de titre", pas de lever de message (false)
            if (zone.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.sousZone)) || zone.getSubFields().stream().anyMatch(subField -> subField.getValue().contains("Complément de titre"))) {
                return false;
            }

            // si la sousZone contient la chaine de caratère "doi.org" OU la chaine de caractère "http", alors lever le message (true)
            if (zone.getSubFields().stream().anyMatch(subField -> subField.getValue().contains("doi.org")) || zone.getSubFields().stream().anyMatch(subField -> subField.getValue().contains("http"))) {
                return true;
            }

            // Si la sousZone est absente (isEmpty) OU si elle ne contient ni "doi.org" ni "http", pas de lever de message (false)
        }


        return false;
    }

    @Override
    public String getZones() {
        return null;
    }
}
