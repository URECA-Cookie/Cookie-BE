package com.cookie.domain.user.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final JobLauncher jobLauncher;
    private final Job genreScoreUpdateJob;

    public SchedulerConfig(JobLauncher jobLauncher, Job genreScoreUpdateJob) {
        this.jobLauncher = jobLauncher;
        this.genreScoreUpdateJob = genreScoreUpdateJob;
    }

    //@Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시
    @Scheduled(cron = "0 */2 * * * ?") // 매 2분마다 실행
    public void runBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 실행 시점의 시간을 파라미터로 추가
                    .toJobParameters();

            jobLauncher.run(genreScoreUpdateJob, jobParameters);
        } catch (Exception e) {
            // 예외 처리 로직
            System.err.println("Batch job failed: " + e.getMessage());
        }
    }
}

