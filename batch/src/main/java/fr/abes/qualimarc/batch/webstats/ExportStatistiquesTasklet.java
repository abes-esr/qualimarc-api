package fr.abes.qualimarc.batch.webstats;

import fr.abes.qualimarc.batch.webstats.correspondance.FamilleDocumentStat;
import fr.abes.qualimarc.batch.webstats.correspondance.RuleSetStat;
import fr.abes.qualimarc.batch.webstats.statanalyses.AnalysesStat;
import fr.abes.qualimarc.core.repository.qualimarc.FamilleDocumentRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;
import fr.abes.qualimarc.core.repository.qualimarc.RuleSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class ExportStatistiquesTasklet implements Tasklet, StepExecutionListener {
    private final RuleSetRepository ruleSetRepository;

    private final FamilleDocumentRepository familleDocumentRepository;

    private final JournalAnalyseRepository journalAnalyseRepository;

    private String uploadPath;

    private Integer annee;
    private Integer mois;

    public ExportStatistiquesTasklet(RuleSetRepository ruleSetRepository, FamilleDocumentRepository familleDocumentRepository, JournalAnalyseRepository journalAnalyseRepository, String uploadPath) {
        this.ruleSetRepository = ruleSetRepository;
        this.familleDocumentRepository = familleDocumentRepository;
        this.journalAnalyseRepository = journalAnalyseRepository;
        this.uploadPath = uploadPath;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        annee = (Integer) stepExecution.getJobExecution().getExecutionContext().get("annee");
        mois = (Integer) stepExecution.getJobExecution().getExecutionContext().get("mois");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("Execute exportStatistiquesTasklet");
        FamilleDocumentStat famille = new FamilleDocumentStat(familleDocumentRepository);
        famille.generate(getFileName("correspondance_famille_document.csv"), getDate());

        RuleSetStat ruleSet = new RuleSetStat(ruleSetRepository);
        ruleSet.generate(getFileName("correspondance_rule_set.csv"), getDate());

        AnalysesStat analysesStat = new AnalysesStat(journalAnalyseRepository, getDate());
        analysesStat.generate(getFileName("analyses.csv"), getDate());

        return RepeatStatus.FINISHED;
    }

    private Date getDate() throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        return formater.parse(this.annee + "-" + this.mois + "-01");
    }

    private String getFileName(String filename) {
        return uploadPath + this.annee + ((this.mois < 10) ? '0' + this.mois.toString() : this.mois.toString()) + "_" + filename;
    }
}
