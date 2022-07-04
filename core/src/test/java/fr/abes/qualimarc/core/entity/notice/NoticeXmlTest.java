package fr.abes.qualimarc.core.entity.notice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoticeXmlTest {

    @Test
    void getTypeDocument() {
        NoticeXml notice = new NoticeXml();
        notice.setLeader("     cam0 22        450 ");
        Assertions.assertEquals("am", notice.getTypeDocument());
    }
}