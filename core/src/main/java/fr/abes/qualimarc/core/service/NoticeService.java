package fr.abes.qualimarc.core.service;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.model.entity.basexml.NoticesAutorite;
import fr.abes.qualimarc.core.model.entity.basexml.NoticesBibio;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.repository.basexml.NoticesAutoriteRepository;
import fr.abes.qualimarc.core.repository.basexml.NoticesBibioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class NoticeService {
    @Autowired
    private NoticesBibioRepository repositoryBiblio;

    @Autowired
    private NoticesAutoriteRepository repositoryAutorite;

    public NoticeXml getBiblioByPpn(String ppn) throws SQLException, IOException, IllegalPpnException {
        if (ppn == null)
            throw new IllegalPpnException("Le PPN ne peut pas être null");
        Optional<NoticesBibio> noticesBibio = repositoryBiblio.getByPpn(ppn);
        if (noticesBibio.isPresent()) {
            return getMapper().readValue(noticesBibio.get().getDataXml().getCharacterStream(), NoticeXml.class);
        }
        throw new IllegalPpnException("le PPN " + ppn + " n'existe pas");
    }

    public NoticeXml getAutoriteByPpn(String ppn) throws SQLException, IOException, IllegalPpnException {
        if (ppn == null)
            throw new IllegalPpnException("Le PPN ne peut pas être null");
        Optional<NoticesAutorite> noticesAutorite = repositoryAutorite.getByPpn(ppn);
        if (noticesAutorite.isPresent()) {
            return getMapper().readValue(noticesAutorite.get().getDataXml().getCharacterStream(), NoticeXml.class);
        }
        throw new IllegalPpnException("le PPN " + ppn + " n'existe pas");
    }

    private XmlMapper getMapper() {
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        return new XmlMapper(module);
    }
}
