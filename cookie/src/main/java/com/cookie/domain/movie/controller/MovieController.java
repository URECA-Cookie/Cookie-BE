package com.cookie.domain.movie.controller;

import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping("{movieId}/reviews")
    public ApiSuccess<?> createReview(@PathVariable(name = "movieId") Long movieId) {
        // TODO: userId JWT 토큰으로 변경
        ReviewOfMovieResponse movieReviews = movieService.getMovieReviewList(movieId);
        return ApiUtil.success(movieReviews);
    }
}
