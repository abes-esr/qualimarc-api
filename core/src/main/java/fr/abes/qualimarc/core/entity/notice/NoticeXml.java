package fr.abes.qualimarc.core.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
        return "Notice {"+ "leader="+ leader+"}";
    }

    //Retourne le type de document de la notice en se basant sur les caractères en position 6 et 7 du leader
    public String getTypeDocument() {
        return this.leader.substring(6, 8);
    }

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
                return "bd";
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

}
