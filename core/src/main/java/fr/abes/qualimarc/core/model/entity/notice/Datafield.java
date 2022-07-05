package fr.abes.qualimarc.core.model.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Datafield {

    @JacksonXmlProperty(isAttribute = true)
    private String tag;

    @JacksonXmlProperty(isAttribute = true)
    private String ind1;

    @JacksonXmlProperty(isAttribute = true)
    private String ind2;

    @JacksonXmlProperty(localName = "subfield")
    private List<SubField> subFields;
}
