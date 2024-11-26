package com.cookie.domain.movie.controller;


import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{movieId}")
    public ApiSuccess<?> getMovieDetails(@PathVariable(name = "movieId") Long movieId) {
        MovieResponse movieDetails = movieService.getMovieDetails(movieId);
        return ApiUtil.success(movieDetails);
    }
  
    @GetMapping("{movieId}/reviews")
    public ApiSuccess<?> getMovieReviewList(@PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        // TODO: userId JWT 토큰으로 변경
        ReviewOfMovieResponse movieReviews = movieService.getMovieReviewList(movieId, 1L, pageable);
        return ApiUtil.success(movieReviews);
    }

    @GetMapping("{movieId}/reviews/spoiler")
    public ApiSuccess<?> getMovieSpoilerReviewList(@PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        // TODO: userId JWT 토큰으로 변경
        ReviewOfMovieResponse movieReviews = movieService.getMovieSpoilerReviewList(movieId, 1L, pageable);
        return ApiUtil.success(movieReviews);

    }

}
