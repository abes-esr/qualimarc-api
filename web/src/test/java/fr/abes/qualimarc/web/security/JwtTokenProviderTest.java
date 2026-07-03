package fr.abes.qualimarc.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

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
}
