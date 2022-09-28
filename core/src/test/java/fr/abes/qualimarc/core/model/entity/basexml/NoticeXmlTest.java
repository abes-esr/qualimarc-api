package fr.abes.qualimarc.core.model.entity.basexml;

import fr.abes.qualimarc.core.exception.noticexml.AuteurNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.IsbnNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.TitreNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.ZoneNotFoundException;
import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
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
        Assertions.assertTrue(notice.isTheseSoutenance());

        subField.setValue("       7  ");
        Assertions.assertTrue(notice.isTheseSoutenance());
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
        Assertions.assertTrue(notice.isTheseRepro());

        subField.setValue("       v  ");
        Assertions.assertTrue(notice.isTheseRepro());


        subField.setValue("");
        Assertions.assertFalse(notice.isTheseRepro());
    }

    @Test
    void getTitre() throws ZoneNotFoundException {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("200");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("a");
        subField.setValue("Titre Test");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);

        Assertions.assertEquals("Titre Test", notice.getTitre());
    }

    @Test
    void getTitreWithoutTitre() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("200");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("b");
        subField.setValue("je ne suis pas un titre");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);

        Assertions.assertThrows(ZoneNotFoundException.class, notice::getTitre);
        Assertions.assertThrows(TitreNotFoundException.class, notice::getTitre);
    }

    @Test
    void getAuteur() throws ZoneNotFoundException {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("200");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("f");
        subField.setValue("Auteur test");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);

        Assertions.assertEquals("Auteur Test", notice.getAuteur());
    }

    @Test
    void getAuteurWithoutAuteur() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("200");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("g");
        subField.setValue("je ne suis pas un auteur");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);

        Assertions.assertThrows(ZoneNotFoundException.class, notice::getAuteur);
        Assertions.assertThrows(AuteurNotFoundException.class, notice::getAuteur);
    }


    @Test
    void getIsbn() throws ZoneNotFoundException {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("010");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("a");
        subField.setValue("1234567890");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);


        NoticeXml notice2 = new NoticeXml();
        notice2.setLeader("");
        List<Datafield> datafields2 = new ArrayList<>();
        Datafield datafield2 = new Datafield();
        datafield2.setTag("010");
        List<SubField> subFields2 = new ArrayList<>();
        SubField subField2 = new SubField();
        subField2.setCode("A");
        subField2.setValue("9781334567890");
        subFields2.add(subField2);
        datafield2.setSubFields(subFields2);
        datafields2.add(datafield2);
        notice2.setDatafields(datafields2);

        Assertions.assertEquals("1234567890", notice.getIsbn());
        Assertions.assertEquals("9781334567890", notice2.getIsbn());
    }

    @Test
    void getIsbnWithoutIsbn() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("010");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("g");
        subField.setValue("je ne suis pas un isbn");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);

        Assertions.assertThrows(ZoneNotFoundException.class, notice::getIsbn);
        Assertions.assertThrows(IsbnNotFoundException.class, notice::getIsbn);
    }
}