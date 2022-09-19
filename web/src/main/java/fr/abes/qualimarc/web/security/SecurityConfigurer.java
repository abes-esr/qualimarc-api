package fr.abes.qualimarc.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * La classe {@code SecurityConfigurer} permet de configurer la sécurité du service web.
 * Cette classe est basée sur le framework Spring avec le module Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    /**
     * Permet de configurer la politique de sécurité du service web tel que :
     * <ul>
     *     <li>Aucune session HTTP n'est créée pour la couche Spring Security.</li>
     *     <li>Les exceptions levées par les filtres sont gérées par le framework Spring Security.</li>
     * </ul>
     *
     * @param http
     * @throws Exception s'il y a une erreur dans la configuration.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().exceptionHandling()
                .and()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers("/api/v1/indexRules").hasAuthority("ANONYMOUS")
                .anyRequest().permitAll();
        http.addFilterBefore(jwtAuthenticationFilter(), AnonymousAuthenticationFilter.class);

    }
}
