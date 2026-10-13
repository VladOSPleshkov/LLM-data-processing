package review.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EntityScan(basePackages = "review.entity")
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean reviewEntityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource dataSource) {
        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "validate");
        return builder
                .dataSource(dataSource)
                .packages("review.entity")
                .persistenceUnit("reviewUnit")
                .properties(props)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier("reviewEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
