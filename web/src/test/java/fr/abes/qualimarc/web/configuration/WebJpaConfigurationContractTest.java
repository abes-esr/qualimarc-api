package fr.abes.qualimarc.web.configuration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class WebJpaConfigurationContractTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "application-dev.properties",
            "application-test.properties",
            "application-prod.properties"
    })
    void profilePropertiesShouldNotForcePostgreSqlDialect(String profileProperties) throws IOException {
        Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(profileProperties));

        assertThat(properties).doesNotContainKeys(
                "spring.jpa.qualimarc.properties.hibernate.dialect",
                "spring.jpa.qualimarc.database-platform"
        );
    }
}
