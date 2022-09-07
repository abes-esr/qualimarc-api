package fr.abes.qualimarc;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.repository.qualimarc.FamilleDocumentRepository;
import fr.abes.qualimarc.core.repository.qualimarc.RuleSetRepository;
import fr.abes.qualimarc.core.repository.qualimarc.RulesRepository;
import fr.abes.qualimarc.core.utils.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class QualimarcAPIApplication implements CommandLineRunner {
    @Autowired
    private FamilleDocumentRepository familleDocumentRepository;

    @Autowired
    private RulesRepository rulesRepository;

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(Arrays.stream(env.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("localhost")))) {
            List<FamilleDocument> familles = familleDocumentRepository.findAll();
            Rule rule1 = new PresenceZone("Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", "011", Priority.P1, familles.stream().filter(f -> !f.getId().equals("BD")).collect(Collectors.toSet()), false);
            Rule rule2 = new PresenceZone("Zone 013  : lorsque la ressource de type Enregistrement sonore (G*) est identifiée par un ISMN, sa transcription est obligatoire.", "013", Priority.P2, familles.stream().filter(f -> f.getId().equals("G")).collect(Collectors.toSet()), false);
            Rule rule3 = new PresenceZone("Zone 101 : l'enregistrement d'un code de langue est obligatoire.", "101", Priority.P2, null, false);
            Rule rule4 = new PresenceSousZone("Document électronique : si la ressource possède un DOI et qu'il est présent sur la ressource, le saisir en 107$a", "013", Priority.P2, familles.stream().filter(f -> f.getId().equals("O")).collect(Collectors.toSet()), "$a", false);
            Rule rule5 = new PresenceSousZone("Zone 101 : puisque la ressource n'est pas de type audiovisuel ni multimédia, la sous-zone $j n'a pas lieu d'être", "101", Priority.P2, familles.stream().filter(f -> !f.getId().equals("B")).collect(Collectors.toSet()), "$j", false);

            List<Rule> rules = new ArrayList<>();
            rules.add(rule1);
            rules.add(rule2);
            rules.add(rule3);
            rules.add(rule4);
            rules.add(rule5);

            rulesRepository.saveAll(rules);
        }
    }
}
