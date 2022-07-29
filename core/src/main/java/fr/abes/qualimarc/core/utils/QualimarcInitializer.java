package fr.abes.qualimarc.core.utils;

import fr.abes.qualimarc.core.model.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.reference.RuleSet;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QualimarcInitializer {
    @EventListener
    public void afterPropertiesSet(ContextRefreshedEvent event){
        initTypesDocuments();
        initRulesSets();
    }

    private void initRulesSets() {
        List<RuleSet> listTemp = new ArrayList<>();
        listTemp.add(new RuleSet(1, "Zones 210/214 (publication, production, diffusion)"));
        listTemp.add(new RuleSet(2, "Implémentations UNM 2022"));
        listTemp.add(new RuleSet(3, "Translitération (présence $6, $7)"));
        listTemp.add(new RuleSet(4, "Zones de données codées (1XX)"));
        listTemp.add(new RuleSet(5, "Zones d'indexation-matière (6XX)"));
        Constants.RULE_SET.addAll(listTemp);
    }

    private void initTypesDocuments() {
        List<FamilleDocument> listTemp = new ArrayList<>();
        listTemp.add(new FamilleDocument("B", "Audiovisuel"));
        listTemp.add(new FamilleDocument("K", "Carte"));
        listTemp.add(new FamilleDocument("O", "Doc Elec"));
        listTemp.add(new FamilleDocument("N", "Enregistrement"));
        listTemp.add(new FamilleDocument("I", "Image"));
        listTemp.add(new FamilleDocument("F", "Manuscrit"));
        listTemp.add(new FamilleDocument("Z", "Multimédia"));
        listTemp.add(new FamilleDocument("V", "Objet"));
        listTemp.add(new FamilleDocument("G", "Musique"));
        listTemp.add(new FamilleDocument("M", "Partition"));
        listTemp.add(new FamilleDocument("BD", "Ressource continue"));
        listTemp.add(new FamilleDocument("A", "Monographie"));
        listTemp.add(new FamilleDocument("TS", "Thèse soutenance"));
        listTemp.add(new FamilleDocument("TR", "Thèse reproduction"));
        Constants.TYPE_DOCUMENT.addAll(listTemp);
    }
}
