package com.cookie.domain.movie.controller;


import com.cookie.domain.category.request.CategoryRequest;
import com.cookie.domain.movie.dto.response.MainPageResponse;
import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{movieId}/{userId}") //userId token으로 변경 필요
    public ResponseEntity<MovieResponse> getMovieDetail(
            @PathVariable(name="movieId") Long movieId,
            @PathVariable(name="userId") Long userId) {
        MovieResponse movieDetail = movieService.getMovieDetails(movieId, userId);
        return ResponseEntity.ok(movieDetail);
    }
  
    @GetMapping("{movieId}/reviews")
    public ApiSuccess<?> getMovieReviewList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        ReviewOfMovieResponse movieReviews = movieService.getMovieReviewList(movieId, userId, pageable);
        return ApiUtil.success(movieReviews);
    }

    @GetMapping("{movieId}/reviews/spoiler")
    public ApiSuccess<?> getMovieSpoilerReviewList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable(name = "movieId") Long movieId, Pageable pageable) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getId() : null;
        ReviewOfMovieResponse movieReviews = movieService.getMovieSpoilerReviewList(movieId, userId, pageable);
        return ApiUtil.success(movieReviews);

    }

    @GetMapping("/{categoryId}/categoryMovies")
    public ResponseEntity<List<MovieSimpleResponse>> getMoviesByCategoryId(@RequestBody CategoryRequest categoryRequest) {
        List<MovieSimpleResponse> movies = movieService.getMoviesByCategory(categoryRequest);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{userId}/recommendations")
    public ApiSuccess<List<MovieSimpleResponse>> getRecommendations(@PathVariable(name="userId") Long userId) {
        List<MovieSimpleResponse> recommendedMovies = movieService.getRecommendedMovies(userId);
        return ApiUtil.success(recommendedMovies);
    }

//    @GetMapping("/mainPage")
//    public ApiSuccess<MainPageResponse> getMainPageInfo(){
//        MainPageResponse mainPageResponse = movieService.getMainPageInfo();
//        return ApiUtil.success(mainPageResponse);
//    }

}
