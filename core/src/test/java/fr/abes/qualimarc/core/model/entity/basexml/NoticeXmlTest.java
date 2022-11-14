package fr.abes.qualimarc.core.model.entity.basexml;

import fr.abes.qualimarc.core.exception.noticexml.AuteurNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.TitreNotFoundException;
import fr.abes.qualimarc.core.exception.noticexml.ZoneNotFoundException;
import fr.abes.qualimarc.core.model.entity.notice.Controlfield;
import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.utils.TypeThese;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
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
    void checkTheseSoutenance() {
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
        Assertions.assertEquals(TypeThese.SOUTENANCE, notice.getTypeThese());

        subField.setValue("       7  ");
        Assertions.assertEquals(TypeThese.SOUTENANCE, notice.getTypeThese());

        subField.setValue("");
        Assertions.assertNull(notice.getTypeThese());
    }

    @Test
    void checkTheseRepro() {
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
        Assertions.assertEquals(TypeThese.REPRO, notice.getTypeThese());

        subField.setValue("       v  ");
        Assertions.assertEquals(TypeThese.REPRO, notice.getTypeThese());


        subField.setValue("");
        Assertions.assertNull(notice.getTypeThese());
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

        Assertions.assertEquals("Auteur test", notice.getAuteur());
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
    void getIsbn() {
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

        Assertions.assertEquals("1234567890", notice.getIsbn());
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

        Assertions.assertNull(notice.getIsbn());
    }

    @Test
    void getOcn() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("034");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("a");
        subField.setValue("(OCoLC)123456789");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);

        Assertions.assertEquals("123456789", notice.getOcn());
    }

    @Test
    void getOcnWithoutOcn() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("");
        List<Datafield> datafields = new ArrayList<>();
        Datafield datafield = new Datafield();
        datafield.setTag("035");
        List<SubField> subFields = new ArrayList<>();
        SubField subField = new SubField();
        subField.setCode("b");
        subField.setValue("je ne suis pas un ocn");
        subFields.add(subField);
        datafield.setSubFields(subFields);
        datafields.add(datafield);
        notice.setDatafields(datafields);
        Assertions.assertNull(notice.getOcn());
    }

    @Test
    void isNoticeDeleted() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("     dam0 22        450 ");
        Assertions.assertTrue(notice.isDeleted());
    }

    @Test
    void getRcr() {
        NoticeXml notice = new NoticeXml();
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("007");
        controlfield.setValue("341725201");
        notice.setControlfields(Lists.newArrayList(controlfield));

        Assertions.assertEquals("341725201", notice.getRcr());
    }

    @Test
    void getDateModificationWith005() throws ParseException {
        NoticeXml notice = new NoticeXml();
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("005");
        controlfield.setValue("20220407143046.000");
        notice.setControlfields(Lists.newArrayList(controlfield));

        Assertions.assertEquals("07/04/2022", notice.getDateModification());
    }

    @Test
    void getPpn() {
        NoticeXml notice = new NoticeXml();
        Controlfield controlfield = new Controlfield();
        controlfield.setTag("001");
        controlfield.setValue("143519379");
        notice.setControlfields(Lists.newArrayList(controlfield));

        Assertions.assertEquals("143519379", notice.getPpn());
    }

}