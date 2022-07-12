package fr.abes.qualimarc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QualimarcAPIApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
