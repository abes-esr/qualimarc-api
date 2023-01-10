package fr.abes.qualimarc.batch.webstats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
public class VerifierParamsTasklet implements Tasklet, StepExecutionListener {
    private Integer annee;
    private Integer mois;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (System.getProperty("annee") != null && System.getProperty("mois") != null) {
            this.annee = Integer.parseInt(System.getProperty("annee"));
            this.mois = Integer.parseInt(System.getProperty("mois"));
        }
        else {
            Calendar dateJour = Calendar.getInstance();
            this.annee = dateJour.get(Calendar.YEAR);
            this.mois = dateJour.get(Calendar.MONTH);
            if (this.mois == 0) {
                this.mois = 12;
                this.annee--;
            }
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("annee", this.annee);
        stepExecution.getJobExecution().getExecutionContext().put("mois", this.mois);
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("execute vérifParamTasklet");
        if ((mois < 1) || (mois > 12)) {
            log.error("Le format du mois est incorrect");
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        Integer anneeInDateJour = Integer.parseInt(format.format(Calendar.getInstance().getTime()));

        if (this.annee > anneeInDateJour) {
            log.error("L'année ne peut pas être supérieure à l'année en court");
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }

        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }
}
