package com.example.config;

import java.util.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

@Configuration
@ComponentScan({"auth", "analysis", "review", "com.shared", "com.example.security"})
@EnableTransactionManagement
@PropertySources({
        @PropertySource(value = "classpath:auth_app.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:analysis_app.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:review_app.properties", ignoreResourceNotFound = true)
})
public class Application {

    private static final String DRIVER = "org.postgresql.Driver";

    // ==================== AUTH DB ====================
    @Value("${auth.db.url:jdbc:postgresql://localhost:5433/auth_db}")
    private String authUrl;

    @Value("${auth.db.username:auth_user}")
    private String authUser;

    @Value("${auth.db.password:passw}")
    private String authPassword;

    @Value("${auth.liquibase.changelog:classpath:changelogs/homework-2/auth_service/changelog-auth.xml}")
    private String authChangelog;

    @Primary
    @Bean(name = "authDataSource")
    public DataSource authDataSource() {
        return createDataSource(authUrl, authUser, authPassword, "AuthHikariPool");
    }

    @Bean
    public SpringLiquibase liquibaseAuth(@Qualifier("authDataSource") DataSource ds) {
        return liquibase(ds, authChangelog);
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            @Qualifier("authDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("auth.entity");
        em.setPersistenceUnitName("authUnit");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        return new JpaTransactionManager(emFactory.getObject());
    }

    // ==================== ANALYSIS DB ====================
    @Value("${analysis.db.url:jdbc:postgresql://localhost:5434/analysis_db}")
    private String analysisUrl;

    @Value("${analysis.db.username:analysis_user}")
    private String analysisUser;

    @Value("${analysis.db.password:passw}")
    private String analysisPassword;

    @Value("${analysis.liquibase.changelog:classpath:changelogs/homework-2/analysis_service/changelog-analysis.xml}")
    private String analysisChangelog;

    @Bean(name = "analysisDataSource")
    public DataSource analysisDataSource() {
        return createDataSource(analysisUrl, analysisUser, analysisPassword, "AnalysisHikariPool");
    }

    @Bean
    public SpringLiquibase liquibaseAnalysis(@Qualifier("analysisDataSource") DataSource ds) {
        return liquibase(ds, analysisChangelog);
    }

    @Bean(name = "analysisEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean analysisEntityManagerFactory(
            @Qualifier("analysisDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("analysis.entity");
        em.setPersistenceUnitName("analysisUnit");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Bean(name = "analysisTransactionManager")
    public PlatformTransactionManager analysisTransactionManager(
            @Qualifier("analysisEntityManagerFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        return new JpaTransactionManager(emFactory.getObject());
    }

    // ==================== REVIEW DB ====================
    @Value("${review.db.url:jdbc:postgresql://localhost:5435/review_db}")
    private String reviewUrl;

    @Value("${review.db.username:review_user}")
    private String reviewUser;

    @Value("${review.db.password:passw}")
    private String reviewPassword;

    @Value("${review.liquibase.changelog:classpath:changelogs/homework-2/review_service/changelog-review.xml}")
    private String reviewChangelog;

    @Bean(name = "reviewDataSource")
    public DataSource reviewDataSource() {
        return createDataSource(reviewUrl, reviewUser, reviewPassword, "ReviewHikariPool");
    }

    @Bean
    public SpringLiquibase liquibaseReview(@Qualifier("reviewDataSource") DataSource ds) {
        return liquibase(ds, reviewChangelog);
    }

    @Bean(name = "reviewEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean reviewEntityManagerFactory(
            @Qualifier("reviewDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("review.entity");
        em.setPersistenceUnitName("reviewUnit");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Bean(name = "reviewTransactionManager")
    public PlatformTransactionManager reviewTransactionManager(
            @Qualifier("reviewEntityManagerFactory") LocalContainerEntityManagerFactoryBean emFactory) {
        return new JpaTransactionManager(emFactory.getObject());
    }
    private DataSource createDataSource(String url, String user, String password, String poolName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(DRIVER);
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setPoolName(poolName);

        // Настройки пула соединений
        ds.setMaximumPoolSize(10);
        ds.setMinimumIdle(2);
        ds.setIdleTimeout(30000);
        ds.setConnectionTimeout(20000);

        return ds;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, String changelog) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changelog);
        return liquibase;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        return properties;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}