package fr.abes.qualimarc.core.model.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

/**
 * Représente une notice au format d'export UnimarcXML
 */
@NoArgsConstructor
@Getter
@Setter
@JacksonXmlRootElement(localName = "record")
public class NoticeXml {

    @JacksonXmlProperty(localName = "leader")
    private String leader;

    @JacksonXmlProperty(localName = "controlfield")
    private List<Controlfield> controlfields;

    @JacksonXmlProperty(localName = "datafield")
    private List<Datafield> datafields;

    @Override
    public String toString() {
        return "Notice {" + "leader=" + leader + "}";
    }

    /**
     * Retourne le type de document de la notice en se basant sur les caractères en position 6 et 7 du leader
     *
     * @return les 2 caractères du code correspondant au type de document
     */
    public String getTypeDocument() {
        if (this.leader.length() >= 9)
            return this.leader.substring(6, 8);
        return "xx";
    }

    /**
     * Analyse le type de document d'une notice pour en déduire la famille de type de document
     *
     * @return famille de type de document
     */
    public String getFamilleDocument() {
        switch (this.getTypeDocument()) {
            //Famille AUDIOVISUEL
            case "gm":
            case "gd":
            case "gc":
            case "ga":
                return "B";
            //FAMILLE CARTE
            case "em":
            case "ed":
            case "fm":
                return "K";
            //FAMILLE DOCUMENT ELECTRONIQUE
            case "lm":
            case "ld":
            case "lc":
            case "la":
                return "O";
            //FAMILLE ENREGISTREMENT
            case "im":
            case "id":
                return "N";
            //FAMILLE IMAGE
            case "km":
            case "kc":
                return "I";
            //FAMILLE MANUSCRIT
            case "bm":
                return "F";
            //FAMILLE MULTIMEDIA
            case "mm":
            case "md":
            case "mc":
                return "Z";
            //FAMILLE MUSIQUE
            case "jm":
            case "jd":
                return "G";
            //FAMILLE OBJET
            case "rm":
                return "V";
            //FAMILLE PARTITION
            case "dm":
            case "cm":
            case "cd":
            case "cc":
                return "M";
            //FAMILLE RESSOURCE CONTINUE
            case "as":
            case "gs":
            case "ls":
            case "ms":
            case "is":
                return "BD";
            //FAMILLE MONOGRAPHIE
            case "am":
            case "ad":
            case "ac":
            case "aa":
                return "A";
            default:
                throw new IllegalTypeDocumentException("Type de document inconnu");
        }
    }

    /**
     * Teste si la notice est de type "thèse reproduite"
     * Vérifie si le caractère v est présent en position 4 à 7 inclus de la zone 105$a Unimarc d'export
     *
     * @return true si la notice est une thèse reproduite false sinon
     */
    public boolean isTheseRepro() {
        Optional<Datafield> zone105 = this.getDatafields().stream().filter(zone -> zone.getTag().equals("105")).findFirst();
        if (zone105.isPresent()) {
            Datafield datafield = zone105.get();
            Optional<SubField> sousZoneA = datafield.getSubFields().stream().filter(sousZone -> sousZone.getCode().equals("a")).findFirst();
            if (sousZoneA.isPresent()) {
                SubField a = sousZoneA.get();
                if (a.getValue().length() > 7) {
                    if (a.getValue().substring(4, 8).contains("v")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Teste si la notice est de type "thèse de soutenance"
     * Vérifie si les caractères m ou 7 sont présents en position 4 à 7 inclus de la zone 105$a Unimarx d'export
     *
     * @return true si la notice est une thèse de soutenance false sinon
     */
    public boolean isTheseSoutenance() {
        Optional<Datafield> zone105 = this.getDatafields().stream().filter(zone -> zone.getTag().equals("105")).findFirst();
        if (zone105.isPresent()) {
            Datafield datafield = zone105.get();
            Optional<SubField> sousZoneA = datafield.getSubFields().stream().filter(sousZone -> sousZone.getCode().equals("a")).findFirst();
            if (sousZoneA.isPresent()) {
                SubField a = sousZoneA.get();
                if (a.getValue().length() > 7) {
                    if (a.getValue().substring(4, 8).contains("m") || a.getValue().substring(4, 8).contains("7")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
