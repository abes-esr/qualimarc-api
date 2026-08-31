package fr.abes.qualimarc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
// FIX incident TEST du 31/08/2026 : active le scheduling pour permettre la purge
// periodique des analyses orphelines (RuleController.purgeFinishedAnalyses).
@EnableScheduling
public class QualimarcAPIApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }
}
