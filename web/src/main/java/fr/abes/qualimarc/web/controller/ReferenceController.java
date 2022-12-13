
package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.utils.TypeThese;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import fr.abes.qualimarc.web.dto.rulesets.RuleSetsRequestDto;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping(value = "/indexRuleSet", consumes = {"text/yaml", "text/yml"})
    public void indexRuleSet(@Valid @RequestBody RuleSetsRequestDto ruleSetsRequestDto) {
        List<RuleSet> ruleSetList = mapper.mapList(ruleSetsRequestDto.getRuleSetWebDtos(), RuleSet.class);
        try {
            service.saveAllRuleSets(ruleSetList);
        }catch (DataIntegrityViolationException ex){
            throw new IllegalArgumentException("Un jeu de règles avec l'identifiant" + StringUtils.substringBetween(ex.getMessage(), "#", "]") + " existe déjà");
        }
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
