package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.model.entity.basexml.NoticesAutorite;
import fr.abes.qualimarc.core.model.entity.basexml.NoticesBibio;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.repository.basexml.NoticesAutoriteRepository;
import fr.abes.qualimarc.core.repository.basexml.NoticesBibioRepository;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.rowset.serial.SerialClob;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {NoticeService.class})
public class NoticeServiceTest {
    @Autowired
    private NoticeService service;

    @MockBean
    private NoticesBibioRepository repositoryBiblio;

    @MockBean
    private NoticesAutoriteRepository repositoryAutorite;

    @Value("classpath:143519379.xml")
    private Resource xmlNoticeBiblio;

    @Value("classpath:02787088X.xml")
    private Resource xmlNoticeAutorite;

    @Test
    void testGetBiblioByPpn() throws IOException, SQLException {
        String xml = IOUtils.toString(new FileInputStream(xmlNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        NoticesBibio notice = new NoticesBibio();
        notice.setId(1);
        notice.setPpn("143519379");
        notice.setDataXml(new SerialClob(xml.toCharArray()));

        Mockito.when(repositoryBiblio.getByPpn("143519379")).thenReturn(Optional.of(notice));

        NoticeXml noticesBibio = service.getBiblioByPpn("143519379");
        Assertions.assertThat(noticesBibio.getLeader()).isEqualTo("     cam0 22        450 ");
        Assertions.assertThat(noticesBibio.getPpn()).isEqualTo("143519379");
        Assertions.assertThat(noticesBibio.getDatafields().stream().filter(datafield -> datafield.getTag().equals("712")).count()).isEqualTo(1);
        Assertions.assertThat(noticesBibio.getDatafields().stream().filter(datafield -> datafield.getTag().startsWith("9")).count()).isEqualTo(0);

        Mockito.when(repositoryBiblio.getByPpn("111111111")).thenThrow(new IllegalPpnException("le PPN 111111111 n'existe pas"));
        assertThrows(IllegalPpnException.class, () -> service.getBiblioByPpn("111111111"));

        assertThrows(IllegalPpnException.class, () -> service.getBiblioByPpn(null));
    }

    @Test
    void testGetAutoriteByPpn() throws IOException, SQLException {
        String xml = IOUtils.toString(new FileInputStream(xmlNoticeAutorite.getFile()), StandardCharsets.UTF_8);
        NoticesAutorite notice = new NoticesAutorite();
        notice.setId(1);
        notice.setPpn("02787088X");
        notice.setDataXml(new SerialClob(xml.toCharArray()));

        Mockito.when(repositoryAutorite.getByPpn("02787088X")).thenReturn(Optional.of(notice));

        Assertions.assertThat(service.getAutoriteByPpn("02787088X").getLeader()).isEqualTo(" cx j22 3 45 ");

        Mockito.when(repositoryAutorite.getByPpn("111111111")).thenThrow(new IllegalPpnException("le PPN 111111111 n'existe pas"));
        assertThrows(IllegalPpnException.class, () -> service.getAutoriteByPpn("111111111"));

        assertThrows(IllegalPpnException.class, () -> service.getAutoriteByPpn(null));
    }
}
