package fr.abes.qualimarc.core.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@AsyncConfig
public class AsyncConfiguration {
    @Value("${spring.task.execution.pool.core-size:2}")
    private Integer coreSize;
    @Value("${spring.task.execution.pool.max-size:5}")
    private Integer maxSize;
    @Value("${spring.task.execution.pool.queue-capacity:100}")
    private Integer queueSize;
    @Value("${spring.task.execution.thread-name-prefix:AsyncThread-}")
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
