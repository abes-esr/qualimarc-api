package fr.abes.qualimarc.core.configuration;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "qualimarcEntityManager",
        basePackages = "fr.abes.qualimarc.core.repository.qualimarc")
@NoArgsConstructor
@QualimarcConfiguration
public class QualimarcDbConfig {
    @Value("${spring.jpa.qualimarc.show-sql}")
    protected String showsql;
    @Value("${spring.jpa.qualimarc.properties.hibernate.dialect}")
    protected String dialect;
    @Value("${spring.jpa.qualimarc.hibernate.ddl-auto}")
    protected String ddlAuto;
    @Value("${spring.jpa.qualimarc.database-platform}")
    protected String platform;

    protected void configHibernate(LocalContainerEntityManagerFactoryBean em) {
        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("spring.jpa.database-platform", platform);
        properties.put("hibernate.show_sql", showsql);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.dialect", dialect);
        properties.put("logging.level.org.hibernate", "DEBUG");
        properties.put("hibernate.type", "trace");
        em.setJpaPropertyMap(properties);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.qualimarc")
    public DataSourceProperties qualimarcDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource qualimarcDataSource() {
        return qualimarcDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean qualimarcEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(qualimarcDataSource());
        em.setPackagesToScan(
                new String[]{"fr.abes.qualimarc.core.model.entity.qualimarc"});
        configHibernate(em);
        return em;
    }
}
