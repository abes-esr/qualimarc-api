package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.utils.TypeThese;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ReferenceController {
    @Autowired
    private ReferenceService service;

    @Autowired
    private UtilsMapper mapper;

    @GetMapping("/getRuleSets")
    public List<RuleSet> getRuleSets() {
        return service.getRuleSets();
    }

    @GetMapping("/getFamillesDocuments")
    public List<FamilleDocumentWebDto> getFamillesDocuments() {
        List<FamilleDocumentWebDto> listToReturn = mapper.mapList(service.getTypesDocuments(), FamilleDocumentWebDto.class);
        for (TypeThese typeThese : EnumUtils.getEnumList(TypeThese.class)) {
            if (typeThese.equals(TypeThese.REPRO))
                listToReturn.add(new FamilleDocumentWebDto("REPRO", "Thèse de reproduction"));
            else
                listToReturn.add(new FamilleDocumentWebDto("SOUT", "Thèse de soutenance"));
        }
        return listToReturn;
    }
}
