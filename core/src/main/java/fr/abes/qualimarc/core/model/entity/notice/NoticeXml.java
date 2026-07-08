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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ReprÃ©sente une notice au format d'export UnimarcXML
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

    private transient Map<String, Controlfield> controlfieldsByTag;

    private transient Map<String, List<Datafield>> datafieldsByTag;

    @Override
    public String toString() {
        return "Notice {" + "leader=" + leader + "}";
    }

    /**
     * RÃ©cupÃ¨re le titre en 200$a
     * @return
     * @throws ZoneNotFoundException
     */
    public String getTitre() throws ZoneNotFoundException {
        return getDatafieldsByTag("200").stream()
                .flatMap(datafield -> datafield.getSubFields().stream())
                .filter(subField -> subField.getCode().equals("a"))
                .map(subField -> subField.getValue().replaceAll("\\p{C}", ""))
                .findFirst()
                .orElseThrow(() -> new TitreNotFoundException("Titre non renseigné"));
    }

    /**
     * RÃ©cupÃ¨re l'auteur en 200 $f
     * @return
     * @throws ZoneNotFoundException
     */
    public String getAuteur() throws ZoneNotFoundException {
        return getDatafieldsByTag("200").stream()
                .flatMap(datafield -> datafield.getSubFields().stream())
                .filter(subField -> subField.getCode().equals("f"))
                .map(SubField::getValue)
                .findFirst()
                .orElseThrow(() -> new AuteurNotFoundException("Auteur non renseigné"));
    }

    /**
     * RÃ©cupÃ¨re l'ISBN en 010 $a
     * @return
     */
    public String getIsbn(){
        return getDatafieldsByTag("010").stream()
                .flatMap(datafield -> datafield.getSubFields().stream())
                .filter(subField -> subField.getCode().equals("a"))
                .map(SubField::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * RÃ©cupÃ¨re l'OCN en 034 $a
     * @return
     */
    public String getOcn() {
        return getDatafieldsByTag("034").stream()
                .flatMap(datafield -> datafield.getSubFields().stream())
                .filter(subField -> subField.getCode().equals("a") && subField.getValue().startsWith("(OCoLC)"))
                .map(SubField::getValue)
                .map(value -> value.substring(7))
                .findFirst()
                .orElse(null);
    }

    /**
     * Indique si la notice est en Ã©tat supprimÃ©e
     * @return
     */
    public boolean isDeleted() {
        return leader.substring(5,6).equals("d");
    }

    /**
     * RÃ©cupÃ¨re la date de modification de la notice : si la notice n'a pas Ã©tÃ© modifiÃ©e la date de crÃ©ation est quand mÃªme en 005
     * @return la date au format dd/MM/yyyy
     * @throws ParseException
     */
    public String getDateModification() throws ParseException {
        DateFormat dateFormatIn = new SimpleDateFormat("yyyyMMdd");
        DateFormat dateFormatOut = new SimpleDateFormat("dd/MM/yyyy");
        Optional<Controlfield> zone005 = getControlfieldByTag("005");
        if (zone005.isPresent()) {
            String dateModif = zone005.get().getValue().substring(0, 8);
            return dateFormatOut.format(dateFormatIn.parse(dateModif));
        }
        return null;
    }

    /**
     * RÃ©cupÃ¨re le RCR du dernier utilisateur ayant modifiÃ© la notice (ou crÃ©Ã© si la notice n'a jamais Ã©tÃ© modifiÃ©e)
     * @return
     */
    public String getRcr() {
        return getControlfieldByTag("007").map(Controlfield::getValue).orElse(null);
    }

    /**
     * Retourne le type de document de la notice en se basant sur les caractÃ¨res en position 6 et 7 du leader
     *
     * @return les 2 caractÃ¨res du code correspondant au type de document
     */
    public String getTypeDocument() {
        if (this.leader.length() >= 9)
            return this.leader.substring(6, 8);
        return "xx";
    }

    /**
     * Analyse le type de document d'une notice pour en dÃ©duire la famille de type de document
     *
     * @return famille de type de document
     */
    public String getFamilleDocument() {
        switch (this.getTypeDocument()) {
            case "gm":
            case "gc":
                return "B";
            case "em":
            case "ed":
            case "fm":
                return "K";
            case "lm":
            case "lc":
                return "O";
            case "im":
            case "id":
                return "N";
            case "km":
            case "kc":
                return "I";
            case "bm":
                return "F";
            case "mm":
            case "mc":
                return "Z";
            case "jm":
                return "G";
            case "rm":
                return "V";
            case "dm":
            case "cm":
            case "cc":
                return "M";
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
            case "ai":
                return "BD";
            case "am":
            case "ac":
                return "A";
            case "aa":
            case "la":
            case "ga":
                return "PC";
            default:
                throw new IllegalTypeDocumentException("Type de document inconnu");
        }
    }

    public String getPpnLieFromZone(String zone, String sousZone) {
        for (Datafield datafield : getDatafieldsByTag(zone)) {
            List<SubField> subFields = datafield.getSubFields().stream()
                    .filter(subField -> subField.getCode().equals(sousZone))
                    .collect(Collectors.toList());
            if (subFields.isEmpty()) {
                return null;
            }
            return subFields.get(0).getValue();
        }
        return null;
    }

    /**
     * Teste le type de thÃ¨se de la notice
     * @return TypeThese.REPRO si la notice est une thÃ¨se de reproduction, TypeThese.SOUTENANCE si la notice est une thÃ¨se de soutenance, null si la notice n'est pas une thÃ¨se
     */
    public TypeThese getTypeThese() {
        Optional<Datafield> zone105Opt = getDatafieldsByTag("105").stream().findFirst();
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
        return getControlfieldByTag("001").map(Controlfield::getValue).orElse(null);
    }

    public void setControlfields(List<Controlfield> controlfields) {
        this.controlfields = controlfields;
        this.controlfieldsByTag = null;
    }

    public void setDatafields(List<Datafield> datafields) {
        this.datafields = datafields;
        this.datafieldsByTag = null;
    }

    private Optional<Controlfield> getControlfieldByTag(String tag) {
        if (controlfields == null || controlfields.isEmpty()) {
            return Optional.empty();
        }
        if (controlfieldsByTag == null) {
            controlfieldsByTag = controlfields.stream()
                    .collect(Collectors.toMap(Controlfield::getTag, controlfield -> controlfield, (left, right) -> left, HashMap::new));
        }
        return Optional.ofNullable(controlfieldsByTag.get(tag));
    }

    private List<Datafield> getDatafieldsByTag(String tag) {
        if (datafields == null || datafields.isEmpty()) {
            return Collections.emptyList();
        }
        if (datafieldsByTag == null) {
            datafieldsByTag = datafields.stream()
                    .collect(Collectors.groupingBy(Datafield::getTag, HashMap::new, Collectors.toList()));
        }
        return datafieldsByTag.getOrDefault(tag, Collections.emptyList());
    }
}
