package com.cookie.domain.search.service;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.search.dto.response.SearchMovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MovieRepository movieRepository;

    public List<SearchMovieResponse> searchMovies(String keyword, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return movies.stream()
                .map(SearchMovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    public List<SearchMovieResponse> searchActors(String keyword, Pageable pageable) {
        Page<Movie> movies = movieRepository.findMoviesByActorName(keyword, pageable);
        return movies.stream()
                .map(SearchMovieResponse::fromMovie)
                .toList();
    }

    public List<SearchMovieResponse> searchDirectors(String keyword, Pageable pageable) {
        Page<Movie> movies = movieRepository.findMoviesByDirectorNameContainingIgnoreCase(keyword, pageable);
        return movies.stream()
                .map(SearchMovieResponse::fromMovie)
                .toList();
    }
}

