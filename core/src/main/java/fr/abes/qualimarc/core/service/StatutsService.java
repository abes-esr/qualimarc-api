package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.repository.basexml.NoticesBibioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@Service
@Slf4j
public class StatutsService {
    @Autowired
    private NoticesBibioRepository noticeRepository;

    @Autowired
    @Qualifier("baseXmlJdbcTemplate")
    private JdbcTemplate baseXmlJdbcTemplate;

    @Autowired
    @Qualifier("qualimarcJdbcTemplate")
    private JdbcTemplate qualimarcJdbcTemplate;

    public boolean getStatutBaseXml() {
        try {
            String objectTest = baseXmlJdbcTemplate.queryForObject("SELECT SYSDATE FROM DUAL", String.class);
            return objectTest != null;
        } catch (DataAccessException e){
            log.info(e.getMessage());
            return false;
        }
    }

    public boolean getStatutBaseQualimarc() {
        try {
            String objectTest = qualimarcJdbcTemplate.queryForObject("SELECT 1", String.class);
            return objectTest != null;
        } catch (DataAccessException e){
            log.info(e.getMessage());
            return false;
        }
    }

    public String getDateLastPpnSynchronised() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return format.format(noticeRepository.findFirstByDateEtatBeforeOrderByDateEtatDesc(new GregorianCalendar()).getDateEtat().getTime());
        } catch (Exception ex) {
            return "Impossible de récupérer le dernier PPN connnu";
        }
    }
}
