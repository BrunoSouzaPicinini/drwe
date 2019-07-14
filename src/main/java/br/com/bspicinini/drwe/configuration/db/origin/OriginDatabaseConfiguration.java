package br.com.bspicinini.drwe.configuration.db.origin;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = OriginDatabaseConfiguration.ORIGIN_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = OriginDatabaseConfiguration.ORIGIN_TRANSACTION_MANAGER,
        basePackages = {"br.com.bspicinini.drwe.repository.origin"}
)
public class OriginDatabaseConfiguration {

    public static final String ORIGIN_ENTITY_MANAGER_FACTORY = "originEntityManagerFactory";
    public static final String ORIGIN_TRANSACTION_MANAGER = "originTransactionManager";
    public static final String ORIGIN_DATA_SOURCE = "originDataSource";

    @Primary
    @Bean(name = ORIGIN_DATA_SOURCE)
    @ConfigurationProperties(prefix = "origin.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = ORIGIN_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(ORIGIN_DATA_SOURCE) DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("br.com.bspicinini.drwe.model.origin")
                .persistenceUnit("origin")
                .build();
    }

    @Primary
    @Bean(name = ORIGIN_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(
            @Qualifier(ORIGIN_ENTITY_MANAGER_FACTORY) EntityManagerFactory
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}

