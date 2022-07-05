package fr.abes.qualimarc.core.model.entity.notice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NoticeXmlTest {

    @Test
    void getTypeDocument() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("     cam0 22        450 ");
        Assertions.assertEquals("am", notice.getTypeDocument());
    }
}