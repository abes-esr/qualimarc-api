package fr.abes.qualimarc.batch;

import fr.abes.qualimarc.core.configuration.AsyncConfig;
import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication
@ComponentScans(value = {
        @ComponentScan(basePackages = {"fr.abes.qualimarc.core.repository"},
                excludeFilters = {
                        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = BaseXMLConfiguration.class)
                },
                includeFilters = {
                        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = QualimarcConfiguration.class)}
        ),
        @ComponentScan(basePackages = {"fr.abes.qualimarc.core.configuration"},
                excludeFilters = {
                        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AsyncConfig.class),
                        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = BaseXMLConfiguration.class)
                })
})
@EntityScan("fr.abes.qualimarc.core.model.entity.qualimarc")
public class Application {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }
}
