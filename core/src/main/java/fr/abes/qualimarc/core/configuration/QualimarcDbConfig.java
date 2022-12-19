package fr.abes.qualimarc.core.configuration;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "qualimarcEntityManager", transactionManagerRef = "qualimarcTransactionManager",
        basePackages = "fr.abes.qualimarc.core.repository.qualimarc")
@EnableTransactionManagement
@NoArgsConstructor
@QualimarcConfiguration
public class QualimarcDbConfig extends AbstractConfig {
    @Value("${spring.jpa.qualimarc.show-sql}")
    protected boolean showsql;
    @Value("${spring.jpa.qualimarc.properties.hibernate.dialect}")
    protected String dialect;
    @Value("${spring.jpa.qualimarc.hibernate.ddl-auto}")
    protected String ddlAuto;
    @Value("${spring.jpa.qualimarc.database-platform}")
    protected String platform;
    @Value("${spring.jpa.qualimarc.generate-ddl}")
    protected boolean generateDdl;
    @Value("${spring.sql.qualimarc.init.mode}")
    protected String initMode;

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.qualimarc")
    public DataSource qualimarcDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean qualimarcEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(qualimarcDataSource());
        em.setPackagesToScan(
                new String[]{"fr.abes.qualimarc.core.model.entity.qualimarc.*"});
        configHibernate(em, platform, showsql, dialect, ddlAuto, generateDdl, initMode);

        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager qualimarcTransactionManager(@Qualifier("qualimarcEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    @Primary
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(name = "qualimarcJdbcTemplate")
    public JdbcTemplate qualimarcJdbcTemplate() {
        return new JdbcTemplate(qualimarcDataSource());
    }
}
