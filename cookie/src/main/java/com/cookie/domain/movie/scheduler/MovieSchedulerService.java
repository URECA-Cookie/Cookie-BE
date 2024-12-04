package com.cookie.domain.movie.scheduler;

import com.cookie.domain.movie.service.MovieLatestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieSchedulerService {

    private final MovieLatestService movieLatestService;

    // 일일 영화 최신화
    @Scheduled(cron = "0 0 1 * * *")
    public void createDailyMovies() {
        try {
            movieLatestService.createDailyMovies();
            log.info("일일 인기 영화 저장 및 배치 완료!");
        } catch (Exception e) {
            log.error("일일 인기 영화 최신화 중 오류 발생: {}", e.getMessage());
        }
    }


    // 주간 영화 인기순위 최신화
    @Scheduled(cron = "0 0 3 * * SUN")
    public void updateWeeklyMovies() {
        try {
            movieLatestService.createMoviesWeek();
            log.info("주간 인기 영화 저장 및 배치 완료!");
        } catch (Exception e) {
            log.error("주간 인기 영화 최신화 중 오류 발생: {}", e.getMessage());
        }
    }
}
