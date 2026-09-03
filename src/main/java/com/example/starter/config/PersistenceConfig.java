package com.example.starter.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Spring Hibernate setup: DataSource + SessionFactory + transaction manager.
 * Switch DB with JVM system property: -Ddb.profile=h2 (default) or -Ddb.profile=oracle
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class PersistenceConfig {

    @Value("${app.entity.package:com.example.starter.domain}")
    private String entityPackage;

    @Bean
    public DataSource dataSource() throws IOException {
        String profile = System.getProperty("db.profile");
        if (!StringUtils.hasText(profile)) {
            profile = loadDefaultProfile();
        }

        Properties props = new Properties();
        try (InputStream in = new ClassPathResource("db-" + profile + ".properties").getInputStream()) {
            props.load(in);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("jdbc.url"));
        config.setUsername(props.getProperty("jdbc.username"));
        config.setPassword(props.getProperty("jdbc.password"));
        config.setDriverClassName(props.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("jdbc.pool.size", "10")));
        config.setPoolName("SpringStarterPool-" + profile);
        return new HikariDataSource(config);
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) throws IOException {
        String profile = System.getProperty("db.profile");
        if (!StringUtils.hasText(profile)) {
            profile = loadDefaultProfile();
        }

        Properties dbProps = new Properties();
        try (InputStream in = new ClassPathResource("db-" + profile + ".properties").getInputStream()) {
            dbProps.load(in);
        }

        Properties hibernateProps = new Properties();
        hibernateProps.put(AvailableSettings.DIALECT, dbProps.getProperty("hibernate.dialect"));
        hibernateProps.put(AvailableSettings.HBM2DDL_AUTO, dbProps.getProperty("hibernate.hbm2ddl.auto", "update"));
        hibernateProps.put(AvailableSettings.SHOW_SQL, dbProps.getProperty("hibernate.show_sql", "true"));
        hibernateProps.put(AvailableSettings.FORMAT_SQL, dbProps.getProperty("hibernate.format_sql", "true"));
        hibernateProps.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "org.springframework.orm.jpa.hibernate.SpringSessionContext");

        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan(entityPackage);
        factoryBean.setHibernateProperties(hibernateProps);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalSessionFactoryBean sessionFactory) {
        return new HibernateTransactionManager(sessionFactory.getObject());
    }

    private String loadDefaultProfile() throws IOException {
        Properties app = new Properties();
        try (InputStream in = new ClassPathResource("application.properties").getInputStream()) {
            app.load(in);
        }
        return app.getProperty("db.profile", "h2");
    }
}
