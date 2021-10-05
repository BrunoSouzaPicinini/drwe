package br.com.bspicinini.drwe.configuration;

import br.com.bspicinini.drwe.configuration.db.DestinationDatabaseConfiguration;
import br.com.bspicinini.drwe.configuration.db.OriginDatabaseConfiguration;
import br.com.bspicinini.drwe.listener.JobCompletionNotificationListener;
import br.com.bspicinini.drwe.model.destination.UserDestination;
import br.com.bspicinini.drwe.model.origin.UserOrigin;
import br.com.bspicinini.drwe.processor.UserProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BathConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BathConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${chunk-size}")
    private Integer chunkSize;

    @Value("${render-page-size}")
    private Integer readerPageSize;

    @Autowired
    @Qualifier(DestinationDatabaseConfiguration.DESTINATION_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean destinationEntityFactory;

    @Autowired
    @Qualifier(OriginDatabaseConfiguration.ORIGIN_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean originEntityFactory;

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step firstStep) throws Exception {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(firstStep)
                .end()
                .build();
    }

    @Bean
    public Step firstStep(StepBuilderFactory stepBuilderFactory, ItemReader<UserOrigin> reader,
                          ItemWriter<UserDestination> writer, ItemProcessor<UserOrigin, UserDestination> processor) {

        return stepBuilderFactory.get("firstStep")
                .<UserOrigin, UserDestination>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean(destroyMethod = "")
    public ItemReader<UserOrigin> reader() throws Exception {
        String jpqlQuery = "select se from UserOrigin se";

        JpaPagingItemReader<UserOrigin> reader = new JpaPagingItemReader<>();
        reader.setQueryString(jpqlQuery);
        reader.setEntityManagerFactory(originEntityFactory.getObject());
        reader.setPageSize(readerPageSize);
        reader.afterPropertiesSet();
        reader.setSaveState(true);

        return reader;
    }

    @Bean
    public ItemProcessor<UserOrigin, UserDestination> processor() {
        return (userOrigin) ->  {
            UserDestination userDestination = new UserDestination(
                    userOrigin.getUserName(),
                    userOrigin.getFirstName(),
                    userOrigin.getLastName(),
                    userOrigin.getGender(),
                    userOrigin.getPassword(),
                    userOrigin.getStatus());


            LOGGER.info("Converting (" + userOrigin + ") into (" + userDestination + ")");

            return userDestination;
        };
    }

    @Bean
    public JdbcBatchItemWriter<UserDestination> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<UserDestination>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO user_details (user_id,user_name,first_name,last_name,gender,password,status) " +
                        "VALUES (:userId,:userName,:firstName,:lastName,:gender,:password,:status)")
                .dataSource(dataSource)
                .build();
    }
}
