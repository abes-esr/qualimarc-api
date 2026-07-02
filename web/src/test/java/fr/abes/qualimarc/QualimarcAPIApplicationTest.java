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
