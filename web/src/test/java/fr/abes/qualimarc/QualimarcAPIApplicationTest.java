package fr.abes.qualimarc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class QualimarcAPIApplicationTest {

    @Test
    void startupDoesNotEmitConfiguredApiToken(CapturedOutput output) {
        String configuredToken = "token-admin-qualimarc";
        String[] args = {
                "--jwt.token=" + configuredToken,
                "--jwt.anonymousUser=qualimarcUser"
        };

        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            QualimarcAPIApplication.main(args);

            assertThat(output).doesNotContain(configuredToken);
            springApplication.verify(() -> SpringApplication.run(QualimarcAPIApplication.class, args));
        }
    }
}
