package fr.abes.qualimarc.core.configuration;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;

public abstract class AbstractConfig {
    protected void configHibernate(LocalContainerEntityManagerFactoryBean em, String platform, boolean showsql, String dialect, String ddlAuto, boolean generateDdl, String initMode) {
        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(generateDdl);
        vendorAdapter.setShowSql(showsql);
        vendorAdapter.setDatabasePlatform(platform);
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);
        properties.put("hibernate.dialect", dialect);
        properties.put("logging.level.org.hibernate", "DEBUG");
        properties.put("hibernate.type", "trace");
        properties.put("spring.sql.init.mode", initMode);
        em.setJpaPropertyMap(properties);
    }
}
