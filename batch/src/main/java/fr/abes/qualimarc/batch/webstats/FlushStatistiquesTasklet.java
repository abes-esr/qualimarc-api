package fr.abes.qualimarc.batch.webstats;

import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalFamilleRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalMessagesRepository;
import fr.abes.qualimarc.core.repository.qualimarc.JournalRuleSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Calendar;
import java.util.Date;

@Slf4j
public class FlushStatistiquesTasklet implements Tasklet, StepExecutionListener {

    private final JournalAnalyseRepository journalAnalyseRepository;

    private final JournalMessagesRepository journalMessagesRepository;

    private final JournalFamilleRepository journalFamilleRepository;

    private final JournalRuleSetRepository journalRuleSetRepository;


    public FlushStatistiquesTasklet(JournalAnalyseRepository journalAnalyseRepository, JournalMessagesRepository journalMessagesRepository, JournalFamilleRepository journalFamilleRepository, JournalRuleSetRepository journalRuleSetRepository) {
        this.journalAnalyseRepository = journalAnalyseRepository;
        this.journalMessagesRepository = journalMessagesRepository;
        this.journalFamilleRepository = journalFamilleRepository;
        this.journalRuleSetRepository = journalRuleSetRepository;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("Execute exportStatistiquesTasklet");

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.YEAR, -2);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dateTwoYearsAgo = calendar.getTime();

        log.info("Delete journalAnalyse before " + dateTwoYearsAgo);
        this.journalAnalyseRepository.deleteAllByDateTimeBefore(dateTwoYearsAgo);
        this.journalFamilleRepository.deleteAllByDateTimeBefore(dateTwoYearsAgo);
        this.journalRuleSetRepository.deleteAllByDateTimeBefore(dateTwoYearsAgo);


        log.info("Delete Message before " + calendar.get(Calendar.YEAR));
        this.journalMessagesRepository.deleteAllByAnneeLessThan(calendar.get(Calendar.YEAR));

        return RepeatStatus.FINISHED;
    }
}
