
package fr.abes.qualimarc.web.controller;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.service.ReferenceService;
import fr.abes.qualimarc.core.utils.TypeThese;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseWebDto;
import fr.abes.qualimarc.web.dto.reference.AnalyseWebDto;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import fr.abes.qualimarc.web.dto.reference.RuleSetResponseWebDto;
import fr.abes.qualimarc.web.dto.rulesets.RuleSetsRequestDto;
import jakarta.validation.Valid;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/indexRuleSet", consumes = {"text/yaml", "text/yml","application/x-yaml"})
    public void indexRuleSet(@Valid @RequestBody RuleSetsRequestDto ruleSetsRequestDto) {
        List<RuleSet> ruleSetList = mapper.mapList(ruleSetsRequestDto.getRuleSetWebDtos(), RuleSet.class);
        try {
            service.saveAllRuleSets(ruleSetList);
        }catch (DataIntegrityViolationException ex){
            throw new IllegalArgumentException("Un jeu de règles avec l'identifiant " + StringUtils.substringBetween(ex.getMessage(), "#", "]") + " existe déjà");
        }
    }

    @GetMapping("/emptyRuleSets")
    public void viderRuleSets() {
        service.viderRulesSet();
    }

    @GetMapping(value = "/getFamillesDocuments", produces = {"application/json"})
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


    @GetMapping(value = "/getAnalyses", produces = {"application/json"})
    public ResultAnalyseWebDto getAnalyse() {
        ResultAnalyseWebDto resultAnalyseWebDto = new ResultAnalyseWebDto();

        AnalyseWebDto quickAnalyse = new AnalyseWebDto("QUICK", "RAPIDE", "... permet de détecter les erreurs les plus urgentes à corriger", service.getNbRulesByAnalyse("QUICK"));
        resultAnalyseWebDto.addAnalyse(quickAnalyse);

        AnalyseWebDto completeAnalyse = new AnalyseWebDto("COMPLETE", "COMPLÈTE", "... permet de détecter toutes les erreurs et incohérences à corriger", service.getNbRulesByAnalyse("COMPLETE"));
        resultAnalyseWebDto.addAnalyse(completeAnalyse);

        List<FamilleDocumentWebDto> familleDocumentWebDtos = mapper.mapList(service.getTypesDocuments(), FamilleDocumentWebDto.class); //TODO: voir si on peut rajouter le nb de règles par famille de document
        for(FamilleDocumentWebDto familleDocumentWebDto : familleDocumentWebDtos){
            familleDocumentWebDto.setNbRules(service.getNbRulesByTypeDocument(familleDocumentWebDto.getId()));
        }
        List<RuleSetResponseWebDto> ruleSetWebDtos = mapper.mapList(service.getRuleSets(), RuleSetResponseWebDto.class);//TODO: voir si on peut rajouter le nb de règles par jeux de regles
        for (RuleSetResponseWebDto ruleSetWebDto : ruleSetWebDtos) {
            ruleSetWebDto.setNbRules(service.getNbRulesByRuleSet(ruleSetWebDto.getId()));
        }


        AnalyseWebDto focusAnalyse = new AnalyseWebDto("FOCUS", "CIBLÉE", "... permet de filtrer, par type de document et/ou par jeux de règles thématiques, un type d'erreur précis à corriger", null, familleDocumentWebDtos, ruleSetWebDtos);

        resultAnalyseWebDto.addAnalyse(focusAnalyse);

        return resultAnalyseWebDto;
    }

    @GetMapping(value = "/getGeneratedId", produces = {"application/json"})
    public Integer getGeneratedId(){
        return (int) (Math.random() * 1000000);
    }
}
