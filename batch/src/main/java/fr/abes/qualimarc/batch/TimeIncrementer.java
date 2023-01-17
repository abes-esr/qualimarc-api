package fr.abes.qualimarc.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

public class TimeIncrementer implements JobParametersIncrementer {
    private static String key = "time";

    /**
     * Increment the time parameter with the currentTimeMillis.
     */
    @Override
    public JobParameters getNext(JobParameters parameters) {

        JobParameters params = (parameters == null ? new JobParameters() : parameters);

        return new JobParametersBuilder(params).addLong(key, System.currentTimeMillis()).toJobParameters();
    }
}
