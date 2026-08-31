package fr.abes.qualimarc.core.configuration;


import com.zaxxer.hikari.HikariDataSource;
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

    // FIX incident TEST du 31/08/2026 : delais maximaux des appels JDBC vers Oracle BaseXML.
    // Avant ce correctif, aucun timeout n'etait defini : quand l'Oracle BaseXML ne repondait
    // plus, le thread d'analyse restait bloque indefiniment sur la lecture du CLOB de la
    // notice, jusqu'a epuiser le pool asynchrone (TaskRejectedException).
    @Value("${qualimarc.basexml.connect-timeout:10000}")
    private int basexmlConnectTimeout;

    @Value("${qualimarc.basexml.read-timeout:60000}")
    private int basexmlReadTimeout;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.basexml")
    public DataSource baseXmlDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        // FIX incident TEST du 31/08/2026 : timeouts transmis au pilote Oracle.
        // - oracle.net.CONNECT_TIMEOUT : duree max (ms) d'etablissement d'une connexion ;
        // - oracle.jdbc.ReadTimeout : duree max (ms) d'attente d'une reponse, y compris
        //   pendant la lecture du CLOB XML de la notice.
        // Une lecture qui depasse ce delai echoue proprement et libere le thread.
        dataSource.addDataSourceProperty("oracle.net.CONNECT_TIMEOUT", basexmlConnectTimeout);
        dataSource.addDataSourceProperty("oracle.jdbc.ReadTimeout", basexmlReadTimeout);
        return dataSource;
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