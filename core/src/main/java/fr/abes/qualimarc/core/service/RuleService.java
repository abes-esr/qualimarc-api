package fr.abes.qualimarc.core.service;

import fr.abes.qualimarc.core.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.entity.rules.Rule;
import fr.abes.qualimarc.core.utils.TypeDocument;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleService {
    public List<String> checkRulesOnNotices(List<NoticeXml> notices, List<Rule> rulesList){
        for (NoticeXml notice : notices) {
            for (Rule rule : rulesList){
                if(rule.getType_documents().stream().anyMatch(type -> notice.getTypeDocument().equals(type))){
                    //TODO traitement de la règle sur la notice
                }
            }
        }
        return null;
    }
}
