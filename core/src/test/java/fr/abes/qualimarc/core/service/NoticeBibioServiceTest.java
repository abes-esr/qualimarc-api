package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.CoreTestConfiguration;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.entity.notice.NoticesBibio;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.sql.rowset.serial.SerialClob;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {NoticeBibioService.class})
class NoticeBibioServiceTest {
    @Autowired
    private NoticeBibioService service;

    @MockBean
    private NoticesBibioRepository repository;

    @Value("classpath:143519379.xml")
    private Resource xmlFileNotice;

    @Test
    void testGetByPpn() throws IOException, SQLException {
        String xml = IOUtils.toString(new FileInputStream(xmlFileNotice.getFile()), StandardCharsets.UTF_8);
        NoticesBibio notice = new NoticesBibio();
        notice.setId(1);
        notice.setPpn("143519379");
        notice.setDataXml(new SerialClob(xml.toCharArray()));

        Mockito.when(repository.getByPpn("143519379")).thenReturn(Optional.of(notice));

        Assertions.assertThat(service.getByPpn("143519379").getLeader()).isEqualTo("     cam0 22        450 ");

        Mockito.when(repository.getByPpn("111111111")).thenThrow(new IllegalPpnException("le PPN 111111111 n'existe pas"));
        assertThrows(IllegalPpnException.class, () -> service.getByPpn("111111111"));

        assertThrows(IllegalPpnException.class, () -> service.getByPpn(null));
    }
}
