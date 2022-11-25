package fr.abes.qualimarc.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    @Value("${spring.task.execution.pool.core-size}")
    private Integer coreSize;
    @Value("${spring.task.execution.pool.max-size}")
    private Integer maxSize;
    @Value("${spring.task.execution.pool.queue-capacity}")
    private Integer queueSize;
    @Value("${spring.task.execution.thread-name-prefix}")
    private String threadName;

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix(threadName);
        executor.initialize();
        return executor;
    }
}
