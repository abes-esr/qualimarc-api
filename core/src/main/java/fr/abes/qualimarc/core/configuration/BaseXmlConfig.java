package fr.abes.qualimarc.core.configuration;


import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "baseXmlEntityManager",
        basePackages = "fr.abes.qualimarc.core.repository.basexml")
@NoArgsConstructor
@BaseXMLConfiguration
public class BaseXmlConfig extends AbstractConfig{
    @Value("${spring.jpa.basexml.show-sql}")
    protected boolean showsql;
    @Value("${spring.jpa.basexml.properties.hibernate.dialect}")
    protected String dialect;
    @Value("${spring.jpa.basexml.hibernate.ddl-auto}")
    protected String ddlAuto;
    @Value("${spring.jpa.basexml.database-platform}")
    protected String platform;
    @Value("${spring.jpa.basexml.generate-ddl}")
    protected boolean generateDdl;
    @Value("${spring.sql.basexml.init.mode}")
    protected String initMode;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.basexml")
    public DataSource baseXmlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean baseXmlEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(baseXmlDataSource());
        em.setPackagesToScan(
                new String[]{"fr.abes.qualimarc.core.model.entity.basexml"});
        configHibernate(em, platform, showsql, dialect, ddlAuto, generateDdl, initMode);
        return em;
    }

    @Bean(name = "baseXmlJdbcTemplate")
    public JdbcTemplate baseXmlJdbcTemplate() {
        return new JdbcTemplate(baseXmlDataSource());
    }
}