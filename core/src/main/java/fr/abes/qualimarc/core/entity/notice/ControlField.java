package fr.abes.qualimarc.core.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControlField {

    @JacksonXmlProperty(isAttribute = true)
    private String tag;

    @JacksonXmlText
    private String value;
}
