package fr.abes.qualimarc.web.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.anonymousUser}")
    private String anonymousUser;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken() {
        Calendar now = new GregorianCalendar(2099, 12, 31);
        Date expiryDate = now.getTime();

        return Jwts.builder()
                .subject(anonymousUser)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .claim("role", "ANONYMOUS")
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;

        } catch (JwtException ex) {
            log.error("Erreur d'authentification: {}", ex.getMessage());
            return false;
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
