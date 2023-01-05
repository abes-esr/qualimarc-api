package fr.abes.qualimarc.batch;

import fr.abes.qualimarc.core.configuration.AsyncConfiguration;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

//TODO : revoir les package à inclure / exclure pour permettre l'injection de dépendances
@SpringBootApplication
@ComponentScan(basePackages = {"fr.abes.qualimarc.core"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = BaseXMLConfiguration.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AsyncConfiguration.class)
})
public class Application {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }
}
