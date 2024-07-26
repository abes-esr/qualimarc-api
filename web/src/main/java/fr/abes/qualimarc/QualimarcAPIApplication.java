package fr.abes.qualimarc;


import fr.abes.qualimarc.web.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class QualimarcAPIApplication implements CommandLineRunner {
    @Value("${jwt.anonymousUser}")
    private String user;

    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));   // It will set UTC timezone
        SpringApplication.run(QualimarcAPIApplication.class, args);
    }

    @Override
    public void run(String... args) {
        initSpringSecurity();
    }

    private void initSpringSecurity() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ANONYMOUS"));
        String token = tokenProvider.generateToken();
        Authentication auth = new AnonymousAuthenticationToken(token, user, roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
        System.out.println(token);
    }
}
