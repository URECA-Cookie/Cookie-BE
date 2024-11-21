package com.cookie.domain.movie.controller;


import com.cookie.domain.movie.dto.response.MovieResponse;
import com.cookie.domain.movie.service.MovieService;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieLikeService;
    private final MovieService movieService;

    @GetMapping("/{movieId}")
    public ResponseEntity<?> getMovieDetails(@PathVariable Long movieId) {
        MovieResponse movieDetails = movieService.getMovieDetails(movieId);
        return ResponseEntity.ok(ApiUtil.success(movieDetails));
    }

}
