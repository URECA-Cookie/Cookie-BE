package com.cookie.domain.search.service;

import com.cookie.domain.movie.entity.MovieMonthRanking;
import com.cookie.domain.movie.repository.MovieMonthRankingRepository;
import com.cookie.domain.search.dto.response.SearchMovieMonthRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchMovieMonthRankingService {

    private final MovieMonthRankingRepository movieMonthRankingRepository;

    @Transactional(readOnly = true)
    public List<SearchMovieMonthRankingResponse> getMoviesMonthRanking() {

        List<MovieMonthRanking> movieMonthRankings = movieMonthRankingRepository.findAll();

        if (movieMonthRankings.isEmpty()) {
            return Collections.emptyList();
        }

        return movieMonthRankings.stream().map(movie -> {
            return SearchMovieMonthRankingResponse.builder()
                    .movieId(movie.getMovie().getId())
                    .movieTitle(movie.getMovie().getTitle())
                    .releaseAt(movie.getMovie().getReleasedAt())
                    .ranking(movie.getRanking())
                    .poster(movie.getMovie().getPoster())
                    .director(movie.getMovie().getDirector().getName())
                    .build();
        }).toList();
    }
}


