package fr.abes.qualimarc.batch;

import fr.abes.qualimarc.batch.webstats.ExportStatistiquesTasklet;
import fr.abes.qualimarc.batch.webstats.FlushStatistiquesTasklet;
import fr.abes.qualimarc.batch.webstats.VerifierParamsTasklet;
import fr.abes.qualimarc.core.repository.qualimarc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@EnableBatchProcessing
@Configuration
public class BatchConfiguration {
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private RuleSetRepository ruleSetRepository;

    @Autowired
    private FamilleDocumentRepository familleDocumentRepository;

    @Autowired
    private JournalAnalyseRepository journalAnalyseRepository;

    @Autowired
    private JournalMessagesRepository journalMessagesRepository;

    @Autowired
    private JournalFamilleRepository journalFamilleRepository;

    @Autowired
    private JournalRuleSetRepository journalRuleSetRepository;

    @Value("${path.statistiques}")
    private String uploadPath;

    @Bean
    public BatchConfigurer configurer(EntityManagerFactory entityManagerFactory) {
        return new QualimarcBatchConfigurer(entityManagerFactory);
    }

    @Bean
    public Job jobExportStatistiques() {
        return jobs.get("exportStatistiques").incrementer(incrementer())
                .start(stepVerifierParams()).on("FAILED").end()
                .from(stepVerifierParams()).on("COMPLETED").to(stepExportStatistiques())
                .build().build();
    }

    @Bean
    public Job jobFlushStatistiques() {
        return jobs.get("flushStatistiques").incrementer(incrementer())
                .start(stepFlushStatistiques()).on("FAILED").end()
                .from(stepFlushStatistiques()).on("COMPLETED").end()
                .build().build();
    }

    private Step stepFlushStatistiques() {
        return steps.get("flushStatistiques")
                .tasklet(new FlushStatistiquesTasklet(journalAnalyseRepository, journalMessagesRepository, journalFamilleRepository, journalRuleSetRepository)).build();
    }

    @Bean
    public Step stepVerifierParams() {
        return steps.get("stepVerifierParams").allowStartIfComplete(true)
                .tasklet(new VerifierParamsTasklet()).build();
    }

    @Bean
    public Step stepExportStatistiques() {
        return steps.get("stepExportStatistiques").allowStartIfComplete(true)
                .tasklet(new ExportStatistiquesTasklet(ruleSetRepository, familleDocumentRepository, journalAnalyseRepository, journalMessagesRepository, journalFamilleRepository, journalRuleSetRepository, uploadPath)).build();
    }


    // ------------------ INCREMENTER ------------------
    protected JobParametersIncrementer incrementer() {
        return new TimeIncrementer();
    }

}
