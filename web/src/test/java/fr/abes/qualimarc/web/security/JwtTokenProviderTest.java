package fr.abes.qualimarc.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String EXPECTED_ANONYMOUS_USER = "jwt.anonymousUser=qualimarcUser";
    private static final String EXPECTED_TOKEN = "jwt.token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJxdWFsaW1hcmNVc2VyIiwiaWF0IjoxNzcwODI4NTU0LCJleHAiOjQxMDUwMzMyMDAsInJvbGUiOiJBTk9OWU1PVVMifQ.sTZjyv0reSj0JgL5KbuXkJ20BxkIoVpjQM8AhcusBYv30Liq0kpaoPFqibWcmo5sd4ZP3H8b90IMisYJJOgeig";

    private JwtTokenProvider createProvider() {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "configuredToken", "token-admin-qualimarc");
        ReflectionTestUtils.setField(provider, "anonymousUser", "qualimarcUser");
        return provider;
    }

    @Test
    void validateTokenReturnsTrueForConfiguredToken() {
        JwtTokenProvider provider = createProvider();

        assertThat(provider.validateToken("token-admin-qualimarc")).isTrue();
    }

    @Test
    void validateTokenReturnsFalseForDifferentToken() {
        JwtTokenProvider provider = createProvider();

        assertThat(provider.validateToken("token-invalide")).isFalse();
    }

    @Test
    void getJwtFromRequestExtractsBearerToken() {
        JwtTokenProvider provider = createProvider();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-admin-qualimarc");

        assertThat(provider.getJwtFromRequest(request)).isEqualTo("token-admin-qualimarc");
    }

    @Test
    void getUsernameFromJwtTokenReturnsConfiguredAnonymousUser() {
        JwtTokenProvider provider = createProvider();

        assertThat(provider.getUsernameFromJwtToken("token-admin-qualimarc")).isEqualTo("qualimarcUser");
    }

    @Test
    void profilePropertiesExposeTheSameJwtContract() throws IOException {
        assertProfileJwtContract("src/main/resources/application-dev.properties");
        assertProfileJwtContract("src/main/resources/application-test.properties");
        assertProfileJwtContract("src/main/resources/application-prod.properties");
    }

    private void assertProfileJwtContract(String relativePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(relativePath));

        assertThat(lines).contains(EXPECTED_ANONYMOUS_USER, EXPECTED_TOKEN);
    }
}
