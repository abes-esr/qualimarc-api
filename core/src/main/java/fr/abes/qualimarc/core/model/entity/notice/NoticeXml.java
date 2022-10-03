package fr.abes.qualimarc.core.model.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import fr.abes.qualimarc.core.exception.noticexml.AuteurNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.IsbnNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.TitreNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.ZoneNotFoundException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.Option;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public String getTitre() throws ZoneNotFoundException {
        Optional<Datafield> zone200 = this.datafields.stream().filter(datafield -> datafield.getTag().equals("200")).findFirst();
        if(zone200.isPresent()){
            Optional<SubField> sousZone_a = zone200.get().getSubFields().stream().filter(subField -> subField.getCode().equals("a")).findFirst();
            if(sousZone_a.isPresent()){
                return sousZone_a.get().getValue();
            }
        }
        throw new TitreNotFoundException("Titre non renseigné");
    }

    public String getAuteur() throws ZoneNotFoundException {
        Optional<Datafield> zone200 = this.datafields.stream().filter(datafield -> datafield.getTag().equals("200")).findFirst();
        if(zone200.isPresent()){
            Optional<SubField> sousZone_f = zone200.get().getSubFields().stream().filter(subField -> subField.getCode().equals("f")).findFirst();
            if(sousZone_f.isPresent()){
                return sousZone_f.get().getValue();
            }
        }
        throw new AuteurNotFoundException("Auteur non renseigné");
    }

    public String getIsbn(){
        Optional<Datafield> zone010 = this.datafields.stream().filter(datafield -> datafield.getTag().equals("010")).findFirst();
        if(zone010.isPresent()){
            Optional<SubField> sousZone_a_A = zone010.get().getSubFields().stream().filter(subField -> subField.getCode().equals("a") || subField.getCode().equals("A")).findFirst();
            if(sousZone_a_A.isPresent()){
                return sousZone_a_A.get().getValue();
            }
        }
        return null;
    }

    public String getOcn() {
        Optional<Datafield> zone034 = this.datafields.stream().filter(datafield -> datafield.getTag().equals("034")).findFirst();
        if(zone034.isPresent()){
            Optional<SubField> sousZonea = zone034.get().getSubFields().stream().filter(subField -> subField.getCode().equals("a") && subField.getValue().startsWith("(OCoLC)")).findFirst();
            if(sousZonea.isPresent()){
                return sousZonea.get().getValue().substring(7);
            }
        }
        return null;
    }

    public boolean isDeleted() {
        return leader.substring(5,6).equals("d");
    }

    public String getDateModification() throws ParseException {
        DateFormat dateFormatIn = new SimpleDateFormat("yyyyMMdd");
        DateFormat dateFormatOut = new SimpleDateFormat("dd/MM/yyyy");
        Optional<Controlfield> zone005 = this.controlfields.stream().filter(zone -> zone.getTag().equals("005")).findFirst();
        if (zone005.isPresent()) {
            String dateModif = zone005.get().getValue().substring(0, 8);
            return dateFormatOut.format(dateFormatIn.parse(dateModif));
        } else {
            Optional<Controlfield> zone004 = this.controlfields.stream().filter(zone -> zone.getTag().equals("004")).findFirst();
            if (zone004.isPresent()) {
                return dateFormatOut.format(dateFormatIn.parse(zone004.get().getValue()));
            }
        }
        return null;
    }

    public String getRcr() {
        return this.controlfields.stream().filter(zone -> zone.getTag().equals("007")).findFirst().get().getValue();
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
        if (this.isTheseSoutenance()) {
            return "TS";
        }
        if (this.isTheseRepro()) {
            return "TR";
        }
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
        Optional<Datafield> zone105Opt = this.getDatafields().stream().filter(zone -> zone.getTag().equals("105")).findFirst();
        if (zone105Opt.isPresent()) {
            Datafield zone105 = zone105Opt.get();
            Optional<SubField> aOpt = zone105.getSubFields().stream().filter(sousZone -> sousZone.getCode().equals("a")).findFirst();
            if (aOpt.isPresent()) {
                SubField a = aOpt.get();
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
        Optional<Datafield> zone105Opt = this.getDatafields().stream().filter(zone -> zone.getTag().equals("105")).findFirst();
        if (zone105Opt.isPresent()) {
            Datafield zone105 = zone105Opt.get();
            Optional<SubField> aOpt = zone105.getSubFields().stream().filter(sousZone -> sousZone.getCode().equals("a")).findFirst();
            if (aOpt.isPresent()) {
                SubField a = aOpt.get();
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
