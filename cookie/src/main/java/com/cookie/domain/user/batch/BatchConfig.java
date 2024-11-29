package com.cookie.domain.user.batch;

import com.cookie.domain.user.entity.DailyGenreScore;
import com.cookie.domain.user.entity.GenreScore;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }



    @Bean
    public Job genreScoreUpdateJob(Step genreScoreUpdateStep) {
        return new JobBuilder("genreScoreUpdateJob", jobRepository)
                .start(genreScoreUpdateStep)
                .build();
    }

    @Bean
    public Step genreScoreUpdateStep(ItemReader<DailyGenreScore> reader,
                                     ItemProcessor<DailyGenreScore, GenreScoreUpdate> processor,
                                     ItemWriter<GenreScoreUpdate> writer) {
        return new StepBuilder("genreScoreUpdateStep", jobRepository)
                .<DailyGenreScore, GenreScoreUpdate>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
