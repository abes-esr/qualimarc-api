package fr.abes.qualimarc.core.model.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import fr.abes.qualimarc.core.exception.noticexml.AuteurNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.TitreNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.ZoneNotFoundException;
import fr.abes.qualimarc.core.utils.TypeThese;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Récupère le titre en 200$a
     * @return
     * @throws ZoneNotFoundException
     */
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

    /**
     * Récupère l'auteur en 200 $f
     * @return
     * @throws ZoneNotFoundException
     */
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

    /**
     * Récupère l'ISBN en 010 $a
     * @return
     */
    public String getIsbn(){
        Optional<Datafield> zone010 = this.datafields.stream().filter(datafield -> datafield.getTag().equals("010")).findFirst();
        if(zone010.isPresent()){
            Optional<SubField> sousZone_a_A = zone010.get().getSubFields().stream().filter(subField -> subField.getCode().equals("a")).findFirst();
            if(sousZone_a_A.isPresent()){
                return sousZone_a_A.get().getValue();
            }
        }
        return null;
    }

    /**
     * Récupère l'OCN en 034 $a
     * @return
     */
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

    /**
     * Indique si la notice est en état supprimée
     * @return
     */
    public boolean isDeleted() {
        return leader.substring(5,6).equals("d");
    }

    /**
     * Récupère la date de modification de la notice : si la notice n'a pas été modifiée la date de création est quand même en 005
     * @return la date au format dd/MM/yyyy
     * @throws ParseException
     */
    public String getDateModification() throws ParseException {
        DateFormat dateFormatIn = new SimpleDateFormat("yyyyMMdd");
        DateFormat dateFormatOut = new SimpleDateFormat("dd/MM/yyyy");
        Optional<Controlfield> zone005 = this.controlfields.stream().filter(zone -> zone.getTag().equals("005")).findFirst();
        if (zone005.isPresent()) {
            String dateModif = zone005.get().getValue().substring(0, 8);
            return dateFormatOut.format(dateFormatIn.parse(dateModif));
        }
        return null;
    }

    /**
     * Récupère le RCR du dernier utilisateur ayant modifié la notice (ou créé si la notice n'a jamais été modifiée)
     * @return
     */
    public String getRcr() {
        Optional<Controlfield> zone007 =  this.controlfields.stream().filter(zone -> zone.getTag().equals("007")).findFirst();
        if (zone007.isPresent())
            return zone007.get().getValue();
        return null;
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
            case "gc":
                return "B";
            //FAMILLE CARTE
            case "em":
            case "ed":
            case "fm":
                return "K";
            //FAMILLE DOCUMENT ELECTRONIQUE
            case "lm":
            case "lc":
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
            case "mc":
                return "Z";
            //FAMILLE MUSIQUE
            case "jm":
                return "G";
            //FAMILLE OBJET
            case "rm":
                return "V";
            //FAMILLE PARTITION
            case "dm":
            case "cm":
            case "cc":
                return "M";
            //FAMILLE RESSOURCE CONTINUE
            case "as":
            case "gs":
            case "ls":
            case "ms":
            case "is":
            case "gd":
            case "ld":
            case "cd":
            case "md":
            case "jd":
            case "ad":
                return "BD";
            //FAMILLE MONOGRAPHIE
            case "am":
            case "ac":
                return "A";
            //FAMILLE PARTIE COMPOSANTE
            case "aa":
            case "la":
            case "ga":
                return "PC";
            default:
                throw new IllegalTypeDocumentException("Type de document inconnu");
        }
    }

    public String getPpnLieFromZone(String zone, String sousZone) {
        List<Datafield> datafields = this.getDatafields().stream().filter(datafield -> datafield.getTag().equals(zone)).collect(Collectors.toList());
        for (Datafield datafield : datafields) {
            List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(sousZone)).collect(Collectors.toList());
            if (subFields.isEmpty())
                return null;
            return subFields.get(0).getValue();
        }
        //pas de zone/sous zone trouvée
        return null;
    }

    /**
     * Teste le type de thèse de la notice
     * @return TypeThese.REPRO si la notice est une thèse de reproduction, TypeThese.SOUTENANCE si la notice est une thèse de soutenance, null si la notice n'est pas une thèse
     */
    public TypeThese getTypeThese() {
        Optional<Datafield> zone105Opt = this.getDatafields().stream().filter(zone -> zone.getTag().equals("105")).findFirst();
        if (zone105Opt.isPresent()) {
            Datafield zone105 = zone105Opt.get();
            Optional<SubField> aOpt = zone105.getSubFields().stream().filter(sousZone -> sousZone.getCode().equals("a")).findFirst();
            if (aOpt.isPresent()) {
                SubField a = aOpt.get();
                if (a.getValue().length() > 7) {
                    if (a.getValue().substring(4, 8).contains("v")) {
                        return TypeThese.REPRO;
                    }
                    if (a.getValue().substring(4, 8).contains("m") || a.getValue().substring(4, 8).contains("7")) {
                        return TypeThese.SOUTENANCE;
                    }
                }
            }
        }
        return null;
    }

    public String getPpn() {
        Optional<Controlfield> ppn = controlfields.stream().filter(cf -> cf.getTag().equals("001")).findFirst();
        return ppn.map(Controlfield::getValue).orElse(null);
    }
}
