package fr.abes.qualimarc.web.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class JwtTokenProvider {

    @Value("${jwt.token:}")
    private String configuredToken;

    @Value("${jwt.anonymousUser}")
    private String anonymousUser;

    public String generateToken() {
        return configuredToken;
    }

    public boolean validateToken(String authToken) {
        if (!StringUtils.hasText(configuredToken) || !StringUtils.hasText(authToken)) {
            return false;
        }
        return MessageDigest.isEqual(
                configuredToken.getBytes(StandardCharsets.UTF_8),
                authToken.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUsernameFromJwtToken(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token API invalide");
        }
        return anonymousUser;
    }
}
