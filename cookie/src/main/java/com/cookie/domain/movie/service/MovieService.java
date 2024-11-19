package com.cookie.domain.movie.service;


import com.cookie.domain.movie.dto.MovieResponse;
import com.cookie.domain.movie.entity.MovieLike;
import com.cookie.domain.movie.repository.MovieLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MovieLikeService {

    private final MovieLikeRepository movieLikeRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional(readOnly = true)
    public List<MovieResponse> getLikedMoviesByUserId(Long userId) {
        List<MovieLike> likedMovies = movieLikeRepository.findAllByUserIdWithMovies(userId);

        return likedMovies.stream()
                .map(movieLike -> MovieResponse.builder()
                        .id(movieLike.getMovie().getId())
                        .title(movieLike.getMovie().getTitle())
                        .poster(movieLike.getMovie().getPoster())
                        .plot(movieLike.getMovie().getPlot())
                        .company(movieLike.getMovie().getCompany())
                        .releasedAt(movieLike.getMovie().getReleasedAt() != null
                                ? movieLike.getMovie().getReleasedAt().format(DATE_FORMATTER)
                                : null) // LocalDateTime -> String 변환
                        .runtime(movieLike.getMovie().getRuntime() + " minutes") // int -> String 변환
                        .score(movieLike.getMovie().getScore())
                        .rating(movieLike.getMovie().getRating().name()) // Enum -> String 변환
                        .build())
                .collect(Collectors.toList());
    }
}


