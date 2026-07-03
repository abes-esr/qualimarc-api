package fr.abes.qualimarc.web.security;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenConfigurationContractTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "application-dev.properties",
            "application-test.properties",
            "application-prod.properties"
    })
    void profilePropertiesResolveJwtTokenFromInjectedEnvironmentValue(String profileProperties) throws IOException {
        Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(profileProperties));
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(new MapPropertySource("injectedEnvironment", Map.of("JWT_TOKEN", "token-admin-injecte")));
        propertySources.addLast(new PropertiesPropertySource("profileProperties", properties));
        PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(propertySources);

        assertThat(resolver.getProperty("jwt.anonymousUser")).isEqualTo("qualimarcUser");
        assertThat(resolver.resolveRequiredPlaceholders(resolver.getProperty("jwt.token"))).isEqualTo("token-admin-injecte");
    }
}
