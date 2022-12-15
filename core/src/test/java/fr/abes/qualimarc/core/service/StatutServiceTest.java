package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.model.entity.basexml.NoticesBibio;
import fr.abes.qualimarc.core.repository.basexml.NoticesBibioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {StatutsService.class})
public class StatutServiceTest {
    @Autowired
    StatutsService service;

    @MockBean
    NoticesBibioRepository noticeRepository;

    @MockBean
    @Qualifier("baseXmlJdbcTemplate")
    private JdbcTemplate baseXmlJdbcTemplate;

    @MockBean
    @Qualifier("qualimarcJdbcTemplate")
    private JdbcTemplate qualimarcJdbcTemplate;

    @Test
    void testGetStatutBaseXmlOk() {
        Mockito.when(baseXmlJdbcTemplate.queryForObject("SELECT SYSDATE FROM DUAL", String.class)).thenReturn("Test");
        Assertions.assertTrue(service.getStatutBaseXml());
    }

    @Test
    void testGetStatutBaseXmlError() {
        Mockito.doThrow(CannotGetJdbcConnectionException.class).when(baseXmlJdbcTemplate).queryForObject("SELECT SYSDATE FROM DUAL", String.class);
        Assertions.assertFalse(service.getStatutBaseXml());
    }

    @Test
    void testGetStatutBaseQualimarcOk() {
        Mockito.when(qualimarcJdbcTemplate.queryForObject("SELECT SYSDATE FROM DUAL", String.class)).thenReturn("Test");
        Assertions.assertTrue(service.getStatutBaseQualimarc());
    }

    @Test
    void testGetStatutBaseQualimarcError() {
        Mockito.doThrow(CannotGetJdbcConnectionException.class).when(qualimarcJdbcTemplate).queryForObject("SELECT SYSDATE FROM DUAL", String.class);
        Assertions.assertFalse(service.getStatutBaseQualimarc());
    }

    @Test
    void testGetDateLastPpnSynchronised() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar now = Calendar.getInstance();
        NoticesBibio notice = new NoticesBibio();
        notice.setDateEtat(now);
        Mockito.when(noticeRepository.findFirstByDateEtatBeforeOrderByDateEtatDesc(Mockito.any())).thenReturn(notice);

        Assertions.assertEquals(format.format(now.getTime()), service.getDateLastPpnSynchronised());
    }

    @Test
    void testGetDateLastPpnSynchronisedError() {
        Mockito.doThrow(CannotGetJdbcConnectionException.class).when(noticeRepository).findFirstByDateEtatBeforeOrderByDateEtatDesc(Mockito.any());
        Assertions.assertEquals("Impossible de récupérer le dernier PPN connnu", service.getDateLastPpnSynchronised());
    }
}
