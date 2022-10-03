package fr.abes.qualimarc;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.repository.qualimarc.FamilleDocumentRepository;
import fr.abes.qualimarc.core.repository.qualimarc.RulesRepository;
import fr.abes.qualimarc.web.security.JwtTokenProvider;
import fr.abes.qualimarc.core.utils.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class QualimarcAPIApplication implements CommandLineRunner {
    @Value("${jwt.anonymousUser")
    private String user;

    @Autowired
    private FamilleDocumentRepository familleDocumentRepository;

    @Autowired
    private RulesRepository rulesRepository;

    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public static void main(String[] args) {
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(Arrays.stream(env.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("localhost")))) {
            List<FamilleDocument> familles = familleDocumentRepository.findAll();
            Rule rule1 = new PresenceZone(1, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", "011", Priority.P1, familles.stream().filter(f -> !f.getId().equals("BD")).collect(Collectors.toSet()), false);
            Rule rule2 = new PresenceZone(2, "Zone 013  : lorsque la ressource de type Enregistrement sonore (G*) est identifiée par un ISMN, sa transcription est obligatoire.", "013", Priority.P2, familles.stream().filter(f -> f.getId().equals("G")).collect(Collectors.toSet()), false);
            Rule rule3 = new PresenceZone(3, "Zone 101 : l'enregistrement d'un code de langue est obligatoire.", "101", Priority.P2, null, false);
            Rule rule4 = new PresenceSousZone(4, "Document électronique : si la ressource possède un DOI et qu'il est présent sur la ressource, le saisir en 107$a", "013", Priority.P2, familles.stream().filter(f -> f.getId().equals("O")).collect(Collectors.toSet()), "$a", false);
            //Rule rule5 = new PresenceSousZone(5, "Zone 101 : puisque la ressource n'est pas de type audiovisuel ni multimédia, la sous-zone $j n'a pas lieu d'être", "101", Priority.P2, familles.stream().filter(f -> !f.getId().equals("B")).collect(Collectors.toSet()), "$j", false);

            List<Rule> rules = new ArrayList<>();
            rules.add(rule1);
            rules.add(rule2);
            rules.add(rule3);
            rules.add(rule4);
            //rules.add(rule5);

            rulesRepository.saveAll(rules);
        }
        initSpringSecurity();
    }

    private void initSpringSecurity() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ANONYMOUS"));
        String token = tokenProvider.generateToken();
        Authentication auth = new AnonymousAuthenticationToken(token, user, roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
        System.out.println(token);
    }
}
