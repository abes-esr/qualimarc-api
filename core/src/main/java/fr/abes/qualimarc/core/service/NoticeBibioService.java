package fr.abes.qualimarc.core.service;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.entity.notice.NoticesBibio;
import fr.abes.qualimarc.core.exception.IllegalPpnException;
import fr.abes.qualimarc.core.repository.basexml.NoticesBibioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class NoticeBibioService {
    @Autowired
    private NoticesBibioRepository repository;

    public NoticeXml getByPpn(String ppn) throws SQLException, IOException {
        if (ppn == null)
            throw new IllegalPpnException("Le PPN ne peut pas être null");
        Optional<NoticesBibio> noticesBibio = repository.getByPpn(ppn);
        if (noticesBibio.isPresent()) {
            JacksonXmlModule module = new JacksonXmlModule();
            module.setDefaultUseWrapper(false);
            XmlMapper xmlMapper = new XmlMapper(module);
            return xmlMapper.readValue(noticesBibio.get().getDataXml().getCharacterStream(), NoticeXml.class);
        }
        throw new IllegalPpnException("le PPN " + ppn + " n'existe pas");
    }
}
