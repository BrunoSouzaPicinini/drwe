package br.com.bspicinini.drwe.configuration;

import br.com.bspicinini.drwe.configuration.db.DestinationDatabaseConfiguration;
import br.com.bspicinini.drwe.configuration.db.OriginDatabaseConfiguration;
import br.com.bspicinini.drwe.listener.JobCompletionNotificationListener;
import br.com.bspicinini.drwe.model.destination.UserDestination;
import br.com.bspicinini.drwe.model.origin.UserOrigin;
import br.com.bspicinini.drwe.processor.UserProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableBatchProcessing
public class BathConfiguration {

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
    public Step firstStep(StepBuilderFactory stepBuilderFactory, ItemReader<UserOrigin> reader,
                          ItemWriter<UserDestination> writer, ItemProcessor<UserOrigin, UserDestination> processor) {

        return stepBuilderFactory.get("firstStep")
                .<UserOrigin, UserDestination> chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step firstStep) throws Exception {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(firstStep)
                .end()
                .build();
    }

    @Bean(destroyMethod="")
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
    public ItemWriter<UserDestination> writer() {
        JpaItemWriter<UserDestination> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(destinationEntityFactory.getObject());
        return writer;
    }

    @Bean
    public UserProcessor processor() {
        return new UserProcessor();
    }
}
