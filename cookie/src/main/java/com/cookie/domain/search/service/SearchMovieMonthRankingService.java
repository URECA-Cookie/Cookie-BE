package com.cookie.domain.search.service;

import com.cookie.domain.movie.entity.MovieCategory;
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

            String releaseYear = movie.getMovie().getReleasedAt().substring(0, 4);
            List<MovieCategory> movieCategories = movie.getMovie().getMovieCategories();
            String genreAgent = movieCategories.isEmpty() ? "N/A" : movieCategories.get(0).getCategory().getSubCategory();

            return SearchMovieMonthRankingResponse.builder()
                    .movieId(movie.getMovie().getId())
                    .movieTitle(movie.getMovie().getTitle())
                    .runtime(movie.getMovie().getRuntime())
                    .ranking(movie.getRanking())
                    .releaseYear(releaseYear)
                    .genreAgent(genreAgent)
                    .certification(movie.getMovie().getCertification())
                    .build();
        }).toList();
    }

}


