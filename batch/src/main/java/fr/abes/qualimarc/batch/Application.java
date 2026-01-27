package fr.abes.qualimarc.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication(scanBasePackages = {"fr.abes.qualimarc"})
@EntityScan("fr.abes.qualimarc.core.model.entity.qualimarc")
@Slf4j
public class Application implements CommandLineRunner {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job jobExportStatistiques;

    @Autowired
    private Job jobFlushStatistiques;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        // Pass the required Job Parameters from here to read it anywhere within
        // Spring Batch infrastructure
        if (args[0].equals("exportStatistiques")) {
            Map<String, String> params = new HashMap<>();
            for (int i=1; i < args.length; i++) {
                String key = args[i].substring(0, args[i].indexOf("="));
                String value = args[i].substring(args[i].indexOf("=") + 1);
                log.debug("Param " + i + " " + key + " / " + value);
                params.put(key, value);
            }
            JobParameters jobParameters = new JobParametersBuilder().addString("annee", params.get("annee"))
                    .addString("mois", params.get("mois")).addLong("time", System.currentTimeMillis()).toJobParameters();

            jobLauncher.run(jobExportStatistiques, jobParameters);
        } else {
            if (args[0].equals("flushStatistiques")) {
                JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
                jobLauncher.run(jobFlushStatistiques, jobParameters);
            }
            else {
                log.error("Le nom du job doit être renseigné en premier paramètre");
            }
        }
    }
}
