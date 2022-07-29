package fr.abes.qualimarc;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.repository.qualimarc.FamilleDocumentRepository;
import fr.abes.qualimarc.core.repository.qualimarc.RuleSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class QualimarcAPIApplication implements CommandLineRunner {
    @Autowired
    private FamilleDocumentRepository familleDocumentRepository;

    @Autowired
    private RuleSetRepository ruleSetRepository;

    public static void main(String[] args) {
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
