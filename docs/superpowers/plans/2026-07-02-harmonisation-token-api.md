# Harmonisation du token API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** supprimer la generation et l'affichage du token d'administration au demarrage, puis faire reposer l'authentification sur un token configure identique entre environnements.

**Architecture:** le code de bootstrap Spring ne cree plus de JWT local. Le fournisseur de securite lit un token admin configure (`jwt.token`) et authentifie uniquement les requetes portant exactement ce bearer token. Les profils `dev`, `test` et `prod` exposent la meme cle de configuration sans embarquer la valeur secrete dans le depot.

**Tech Stack:** Java 21, Spring Boot 3, Spring Security, JUnit 5, Mockito

---

### Task 1: Cadrer le nouveau contrat du fournisseur de token

**Files:**
- Create: `web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java`
- Modify: `web/src/main/java/fr/abes/qualimarc/web/security/JwtTokenProvider.java`
- Test: `web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java`

- [ ] **Step 1: Write the failing test**

```java
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl web -Dtest=JwtTokenProviderTest test`
Expected: FAIL because `configuredToken` does not exist yet and `validateToken` still expects a signed JWT.

- [ ] **Step 3: Write minimal implementation**

```java
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
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -pl web -Dtest=JwtTokenProviderTest test`
Expected: PASS with 4 tests green.

- [ ] **Step 5: Commit**

```bash
git add web/src/main/java/fr/abes/qualimarc/web/security/JwtTokenProvider.java web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java
git commit -m "test: verrouiller le fournisseur du token API"
```

### Task 2: Retirer la generation du token au demarrage

**Files:**
- Create: `web/src/test/java/fr/abes/qualimarc/QualimarcAPIApplicationTest.java`
- Modify: `web/src/main/java/fr/abes/qualimarc/QualimarcAPIApplication.java`
- Test: `web/src/test/java/fr/abes/qualimarc/QualimarcAPIApplicationTest.java`

- [ ] **Step 1: Write the failing test**

```java
package fr.abes.qualimarc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import static org.assertj.core.api.Assertions.assertThat;

class QualimarcAPIApplicationTest {

    @Test
    void applicationDoesNotImplementCommandLineRunnerAnymore() {
        assertThat(CommandLineRunner.class.isAssignableFrom(QualimarcAPIApplication.class)).isFalse();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl web -Dtest=QualimarcAPIApplicationTest test`
Expected: FAIL because `QualimarcAPIApplication` currently implements `CommandLineRunner`.

- [ ] **Step 3: Write minimal implementation**

```java
package fr.abes.qualimarc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class QualimarcAPIApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -pl web -Dtest=QualimarcAPIApplicationTest test`
Expected: PASS with 1 test green.

- [ ] **Step 5: Commit**

```bash
git add web/src/main/java/fr/abes/qualimarc/QualimarcAPIApplication.java web/src/test/java/fr/abes/qualimarc/QualimarcAPIApplicationTest.java
git commit -m "fix: supprimer la generation du token au demarrage"
```

### Task 3: Harmoniser la configuration des profils

**Files:**
- Modify: `web/src/main/resources/application-dev.properties`
- Modify: `web/src/main/resources/application-test.properties`
- Modify: `web/src/main/resources/application-prod.properties`
- Test: `web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java`

- [ ] **Step 1: Write the failing configuration expectation in the existing test**

Add this test to `web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java`:

```java
import java.nio.file.Files;
import java.nio.file.Path;

@Test
void profilePropertyFilesExposeTheSameJwtKeys() throws Exception {
    String expectedAnonymousUserLine = "jwt.anonymousUser=qualimarcUser";
    String expectedTokenLine = "jwt.token=${JWT_TOKEN:}";

    assertThat(Files.readString(Path.of("src/main/resources/application-dev.properties"))).contains(expectedAnonymousUserLine, expectedTokenLine);
    assertThat(Files.readString(Path.of("src/main/resources/application-test.properties"))).contains(expectedAnonymousUserLine, expectedTokenLine);
    assertThat(Files.readString(Path.of("src/main/resources/application-prod.properties"))).contains(expectedAnonymousUserLine, expectedTokenLine);
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl web -Dtest=JwtTokenProviderTest test`
Expected: FAIL because the profile files still declare `jwt.secret` and do not expose `jwt.token`.

- [ ] **Step 3: Write minimal implementation**

In each profile file, replace the JWT section with:

```properties
jwt.anonymousUser=qualimarcUser
jwt.token=${JWT_TOKEN:}
```

Apply the replacement in:

```properties
web/src/main/resources/application-dev.properties
web/src/main/resources/application-test.properties
web/src/main/resources/application-prod.properties
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -pl web -Dtest=JwtTokenProviderTest test`
Expected: PASS with 5 tests green.

- [ ] **Step 5: Commit**

```bash
git add web/src/main/resources/application-dev.properties web/src/main/resources/application-test.properties web/src/main/resources/application-prod.properties web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java
git commit -m "fix: harmoniser la configuration du token API"
```

### Task 4: Verification finale du module web

**Files:**
- Modify: `docs/superpowers/plans/2026-07-02-harmonisation-token-api.md`
- Test: `web/src/test/java/fr/abes/qualimarc/QualimarcAPIApplicationTest.java`
- Test: `web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java`

- [ ] **Step 1: Run the focused test suite**

Run: `mvn -pl web -Dtest=QualimarcAPIApplicationTest,JwtTokenProviderTest test`
Expected: PASS with the bootstrap and token tests green.

- [ ] **Step 2: Run the module test suite**

Run: `mvn -pl web test`
Expected: PASS for the `web` module with no regression on controllers and mappers.

- [ ] **Step 3: Inspect git state**

Run: `git status --short`
Expected: no modified tracked files except this plan if you decide to tick boxes manually.

- [ ] **Step 4: Commit the completed implementation**

```bash
git add web/src/main/java/fr/abes/qualimarc/QualimarcAPIApplication.java web/src/main/java/fr/abes/qualimarc/web/security/JwtTokenProvider.java web/src/main/resources/application-dev.properties web/src/main/resources/application-test.properties web/src/main/resources/application-prod.properties web/src/test/java/fr/abes/qualimarc/QualimarcAPIApplicationTest.java web/src/test/java/fr/abes/qualimarc/web/security/JwtTokenProviderTest.java
git commit -m "fix: supprimer le token genere au demarrage"
```
