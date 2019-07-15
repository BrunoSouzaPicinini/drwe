package br.com.bspicinini.drwe.configuration.db;

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
        entityManagerFactoryRef = DestinationDatabaseConfiguration.DESTINATION_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = DestinationDatabaseConfiguration.DESTINATION_TRANSACTION_MANAGER,
        basePackages = {"br.com.bspicinini.drwe.repository.destination"}
)
public class DestinationDatabaseConfiguration {

    public static final String DESTINATION_ENTITY_MANAGER_FACTORY = "destinationEntityManagerFactory";
    public static final String DESTINATION_TRANSACTION_MANAGER = "destinationTransactionManager";
    public static final String DESTINATION_DATA_SOURCE = "dataSource";

    @Primary
    @Bean(name = DESTINATION_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = DESTINATION_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean destinationEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(DESTINATION_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("br.com.bspicinini.drwe.model.destination")
                .persistenceUnit("destination")
                .build();
    }

    @Primary
    @Bean(name = DESTINATION_TRANSACTION_MANAGER)
    public PlatformTransactionManager destinationTransactionManager(
            @Qualifier(DESTINATION_ENTITY_MANAGER_FACTORY) EntityManagerFactory
                    destinationEntityManagerFactory
    ) {
        return new JpaTransactionManager(destinationEntityManagerFactory);
    }

}
