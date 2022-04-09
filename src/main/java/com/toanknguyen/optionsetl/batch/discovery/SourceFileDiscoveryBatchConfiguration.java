package com.toanknguyen.optionsetl.batch.discovery;

import com.toanknguyen.optionsetl.batch.config.BatchConfiguration;
import com.toanknguyen.optionsetl.batch.discovery.reader.FileNameItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class SourceFileDiscoveryBatchConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SourceFileDiscoveryBatchConfiguration.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public SourceFileDiscoveryBatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public ItemReader<String> sourceFileDiscoveryReader(
            @Value("file:${optionsetl.source.directory}/${optionsetl.source.pattern}") String sourceFilePathPattern,
            FileNameItemReader fileNameItemReader
    ) throws IOException {
        logger.debug("sourceFilePathPattern: {}", sourceFilePathPattern);
        return new MultiResourceItemReaderBuilder<String>()
                .name("sourceFileDiscoveryReader")
                .resources(new PathMatchingResourcePatternResolver().getResources(sourceFilePathPattern))
                .delegate(fileNameItemReader)
                .build();
    }

    @Bean
    public ItemWriter<String> sourceFileDiscoveryWriter(DataSource dataSource) {
        logger.trace("sourceFileDiscoveryWriter");
        return new JdbcBatchItemWriterBuilder<String>()
                .dataSource(dataSource)
                .sql("INSERT INTO processed_files (file_name) VALUES (:fileName) ON CONFLICT DO NOTHING;")
                .itemSqlParameterSourceProvider(fn -> new MapSqlParameterSource().addValue("fileName", fn))
                .assertUpdates(false)
                .build();
    }

    @Bean
    public Job discoverFilesJob(Step discoverFilesStep) {
        logger.trace("discover files job");
        return jobBuilderFactory.get("discoverFilesJob")
                .incrementer(new RunIdIncrementer())
                .flow(discoverFilesStep)
                .end()
                .build();
    }

    @Bean
    public Step discoverFilesStep(
            ItemReader<String> sourceFileDiscoveryReader,
            ItemWriter<String> sourceFileDiscoveryWriter
    ) {
        logger.trace("discover files step");
        return stepBuilderFactory.get("discoverFilesStep")
                .<String, String> chunk(1000)
                .reader(sourceFileDiscoveryReader)
                .writer(sourceFileDiscoveryWriter)
                .build();
    }

}
