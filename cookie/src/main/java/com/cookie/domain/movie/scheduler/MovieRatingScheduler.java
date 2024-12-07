package com.cookie.domain.movie.scheduler;

import com.cookie.domain.movie.service.MovieRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieRatingScheduler {

    private final MovieRatingService movieRatingService;

    @Scheduled(cron = "0 0 4 * * ?") // 매일 새벽 4시
    public void updateMovieRatingsAt4AM() {
        movieRatingService.updateMovieRatings();
        log.info("영화 평점 정보가 갱신되었습니다.");
    }

}
