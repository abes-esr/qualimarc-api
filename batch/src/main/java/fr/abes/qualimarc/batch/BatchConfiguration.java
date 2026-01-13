package fr.abes.qualimarc.batch;

import fr.abes.qualimarc.batch.webstats.ExportStatistiquesTasklet;
import fr.abes.qualimarc.batch.webstats.FlushStatistiquesTasklet;
import fr.abes.qualimarc.batch.webstats.VerifierParamsTasklet;
import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import fr.abes.qualimarc.core.repository.qualimarc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@QualimarcConfiguration
public class BatchConfiguration {
    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

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
    public Job jobExportStatistiques() {
        return new JobBuilder("exportStatistiques", jobRepository)
                .incrementer(incrementer())
                .start(stepVerifierParams()).on("FAILED").end()
                .from(stepVerifierParams()).on("COMPLETED").to(stepExportStatistiques())
                .end()
                .build();
    }

    @Bean
    public Job jobFlushStatistiques() {
        return new JobBuilder("flushStatistiques", jobRepository)
                .incrementer(incrementer())
                .start(stepFlushStatistiques()).on("FAILED").end()
                .from(stepFlushStatistiques()).on("COMPLETED").end()
                .build()
                .build();
    }

    private Step stepFlushStatistiques() {
        return new StepBuilder("flushStatistiques", jobRepository)
                .tasklet(new FlushStatistiquesTasklet(journalAnalyseRepository, journalMessagesRepository, journalFamilleRepository, journalRuleSetRepository), transactionManager)
                .build();
    }

    @Bean
    public Step stepVerifierParams() {
        return new StepBuilder("stepVerifierParams", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(new VerifierParamsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step stepExportStatistiques() {
        return new StepBuilder("stepExportStatistiques", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(new ExportStatistiquesTasklet(ruleSetRepository, familleDocumentRepository, journalAnalyseRepository, journalMessagesRepository, journalFamilleRepository, journalRuleSetRepository, uploadPath), transactionManager)
                .build();
    }


    // ------------------ INCREMENTER ------------------
    protected JobParametersIncrementer incrementer() {
        return new TimeIncrementer();
    }

}
