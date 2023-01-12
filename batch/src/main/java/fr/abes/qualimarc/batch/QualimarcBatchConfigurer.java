package fr.abes.qualimarc.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Slf4j
public class QualimarcBatchConfigurer implements BatchConfigurer {
    private final EntityManagerFactory entityManagerFactory;

    private PlatformTransactionManager transactionManager;

    private JobRepository jobRepository;

    private JobLauncher jobLauncher;

    private JobExplorer jobExplorer;

    @Autowired
    private DataSource qualimarcDataSource;

    public QualimarcBatchConfigurer(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public JobRepository getJobRepository() throws Exception {
        return this.jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return this.transactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        return this.jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    @PostConstruct
    public void initialize() {
        try {
            if (this.entityManagerFactory == null) {
                throw new IllegalStateException("Impossible d'initialiser Spring batch, entity manager null");
            }
            else {
                this.transactionManager = new JpaTransactionManager(this.entityManagerFactory);
            }
            // Jobrepository
            JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
            factory.setDataSource(qualimarcDataSource);
            factory.setTransactionManager(transactionManager);
            factory.setDatabaseType(DatabaseType.POSTGRES.name());
            factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
            factory.setTablePrefix("BATCH_");
            factory.setValidateTransactionState(false);
            this.jobRepository = factory.getObject();

            // jobLauncher:
            SimpleJobLauncher jobLauncherParam = new SimpleJobLauncher();
            jobLauncherParam.setJobRepository(getJobRepository());
            jobLauncherParam.setTaskExecutor(new SyncTaskExecutor());
            jobLauncherParam.afterPropertiesSet();
            this.jobLauncher = jobLauncherParam;

            // jobExplorer:
            JobExplorerFactoryBean jobExplorerFactory = new JobExplorerFactoryBean();
            jobExplorerFactory.setDataSource(qualimarcDataSource);
            jobExplorerFactory.setTablePrefix("BATCH_");
            jobExplorerFactory.afterPropertiesSet();
            this.jobExplorer = jobExplorerFactory.getObject();

        } catch (Exception ex) {
            throw new IllegalStateException("Impossible d'initialiser Spring Batch", ex);
        }
    }
}
