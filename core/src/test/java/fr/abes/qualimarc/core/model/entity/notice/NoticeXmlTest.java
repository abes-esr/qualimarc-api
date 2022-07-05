package fr.abes.qualimarc.core.model.entity.notice;

import fr.abes.qualimarc.core.exception.IllegalTypeDocumentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class NoticeXmlTest {

    @Test
    void getTypeDocument() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("     cam0 22        450 ");
        Assertions.assertEquals("am", notice.getTypeDocument());
    }

    @Test
    void getTheseSoutenance() {
        NoticeXml notice = new NoticeXml();
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("105");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("a");
        subField.setValue("    m     ");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);
        Assertions.assertEquals("TS", notice.getFamilleDocument());

        subField.setValue("       7  ");
        Assertions.assertEquals("TS", notice.getFamilleDocument());
    }

    @Test
    void getTheseRepro() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("105");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("a");
        subField.setValue("    v     ");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);
        Assertions.assertEquals("TR", notice.getFamilleDocument());

        subField.setValue("       v  ");
        Assertions.assertEquals("TR", notice.getFamilleDocument());

        subField.setValue("");
        Assertions.assertThrows(IllegalTypeDocumentException.class, () -> notice.getFamilleDocument());
    }
}