package fr.abes.qualimarc.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfigurer {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // si tu as un bean CorsConfigurationSource, il sera utilisé
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/indexRules").hasAuthority("ANONYMOUS")
                        .requestMatchers("/api/v1/indexComplexRules").hasAuthority("ANONYMOUS")
                        .requestMatchers("/api/v1/emptyRules").hasAuthority("ANONYMOUS")
                        .requestMatchers("/api/v1/indexRuleSet").hasAuthority("ANONYMOUS")
                        .requestMatchers("/api/v1/emptyRuleSets").hasAuthority("ANONYMOUS")
                        .anyRequest().permitAll()
                );

        // Ajout du filtre JWT
        http.addFilterBefore(jwtAuthenticationFilter(), AnonymousAuthenticationFilter.class);

        return http.build();
    }
}