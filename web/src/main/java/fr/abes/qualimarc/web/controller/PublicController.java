package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.service.NoticeBibioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class PublicController {
    @Autowired
    private NoticeBibioService service;

    @GetMapping("/{ppn}")
    public NoticeXml getPpn(@PathVariable String ppn) throws IOException, SQLException {
        return service.getByPpn(ppn);
    }

}
