package fr.abes.qualimarc;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.LinkedRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.NombreSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.repository.qualimarc.FamilleDocumentRepository;
import fr.abes.qualimarc.core.repository.qualimarc.ComplexRulesRepository;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.web.security.JwtTokenProvider;
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
import java.util.TimeZone;
import java.util.stream.Collectors;

@SpringBootApplication
public class QualimarcAPIApplication implements CommandLineRunner {
    @Value("${jwt.anonymousUser")
    private String user;

    @Autowired
    private FamilleDocumentRepository familleDocumentRepository;

    @Autowired
    private ComplexRulesRepository complexRulesRepository;

    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));   // It will set UTC timezone
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(Arrays.stream(env.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("localhost")))) {
            List<FamilleDocument> familles = familleDocumentRepository.findAll();
            ComplexRule rule1 = new ComplexRule(1, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, familles.stream().filter(f -> !f.getId().equals("BD")).collect(Collectors.toSet()), new PresenceZone(1, "011", false));
            ComplexRule rule2 = new ComplexRule(2, "Zone 013  : lorsque la ressource de type Enregistrement sonore (G*) est identifiée par un ISMN, sa transcription est obligatoire.", Priority.P2, familles.stream().filter(f -> f.getId().equals("G")).collect(Collectors.toSet()), new PresenceZone(2, "013", false));
            ComplexRule rule3 = new ComplexRule(3, "Zone 101 : l'enregistrement d'un code de langue est obligatoire.", Priority.P2, new PresenceZone(3, "101", false));

            ComplexRule rule4 = new ComplexRule(4, "Document électronique : si la ressource possède un DOI et qu'il est présent sur la ressource, le saisir en 107$a", Priority.P2, new PresenceSousZone(4, "101", "a", true));
            LinkedRule rule5 = new LinkedRule( new NombreSousZone(5, "034", "a", "200", "a"), BooleanOperateur.OU, rule4, 1);
            LinkedRule rule6 = new LinkedRule( new PresenceZone(6, "011", false), BooleanOperateur.OU, rule4, 2);
            LinkedRule rule7 = new LinkedRule( new PresenceZone(7, "011", false), BooleanOperateur.OU, rule4, 3);
            LinkedRule rule8 = new LinkedRule( new PresenceZone(8, "011", false), BooleanOperateur.OU, rule4, 4);

            rule4.addOtherRule(rule5);
            rule4.addOtherRule(rule6);
            rule4.addOtherRule(rule7);
            rule4.addOtherRule(rule8);

            ComplexRule rule9 = new ComplexRule(9, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(9, "011", false));
            ComplexRule rule10 = new ComplexRule(10, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(10, "011", false));
            ComplexRule rule11 = new ComplexRule(11, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(11, "011", false));
            ComplexRule rule12 = new ComplexRule(12, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(12, "011", false));
            ComplexRule rule13 = new ComplexRule(13, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(13, "011", false));
            ComplexRule rule14 = new ComplexRule(14, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(14, "011", false));
            ComplexRule rule15 = new ComplexRule(15, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(15, "011", false));
            ComplexRule rule16 = new ComplexRule(16, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(16, "011", false));
            ComplexRule rule17 = new ComplexRule(17, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(17, "011", false));
            ComplexRule rule18 = new ComplexRule(18, "Zone 011 : à supprimer car un numéro ISSN ne peut apparaître que dans une notice de ressource continue.", Priority.P1, new PresenceZone(18, "011", false));

            List<ComplexRule> rules = new ArrayList<>();
            rules.add(rule1);
            rules.add(rule2);
            rules.add(rule3);
            rules.add(rule4);
            rules.add(rule9);
            rules.add(rule10);
            rules.add(rule11);
            rules.add(rule12);
            rules.add(rule13);
            rules.add(rule14);
            rules.add(rule15);
            rules.add(rule16);
            rules.add(rule17);
            rules.add(rule18);


            //rulesRepository.saveAll(rules);
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
