package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.repository.basexml.NoticesBibioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class StatutsService {
    private static final String LAST_PPN_SYNC_FALLBACK = "Impossible de recuperer le dernier PPN connu";

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
        } catch (DataAccessException e) {
            log.info(e.getMessage());
            return false;
        }
    }

    public boolean getStatutBaseQualimarc() {
        try {
            String objectTest = qualimarcJdbcTemplate.queryForObject("SELECT 1", String.class);
            return objectTest != null;
        } catch (DataAccessException e) {
            log.info(e.getMessage());
            return false;
        }
    }

    public String getDateLastPpnSynchronised() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date lastSyncDate = noticeRepository.findLatestDateEtat();
            if (lastSyncDate == null) {
                return LAST_PPN_SYNC_FALLBACK;
            }
            return format.format(lastSyncDate);
        } catch (Exception ex) {
            log.warn("Impossible de recuperer le dernier PPN synchronise depuis BaseXML", ex);
            return LAST_PPN_SYNC_FALLBACK;
        }
    }
}
