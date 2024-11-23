package com.cookie.admin.service;

import com.cookie.admin.dto.response.AdminMoviesResponse;
import com.cookie.admin.dto.response.MoviesResponse;
import com.cookie.admin.exception.MovieNotFoundException;
import com.cookie.admin.repository.MovieActorRepository;
import com.cookie.admin.repository.MovieDirectorRepository;
import com.cookie.admin.repository.MovieRepository;
import com.cookie.domain.director.entity.Director;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieActor;
import com.cookie.domain.movie.entity.MovieDirector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminMovieSearchService {

    private final MovieRepository movieRepository;
    private final MovieActorRepository movieActorRepository;
    private final MovieDirectorRepository movieDirectorRepository;

    @Transactional(readOnly = true)
    public AdminMoviesResponse getMoviesByName(String movieName, Integer pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber - 1, 10);
        Page<Movie> moviePages = movieRepository.findMovieByTitle(movieName, pageable);
        List<Movie> movies = moviePages.getContent();
        int totalPage = moviePages.getTotalPages();

        List<MoviesResponse> moviesResponses = getMoviesPage(moviePages, movies);

        return AdminMoviesResponse.builder()
                .currentPage(pageNumber)
                .results(moviesResponses)
                .totalPages(totalPage)
                .build();
    }

    @Transactional(readOnly = true)
    public AdminMoviesResponse getMovies(Integer pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber -1, 10);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();
        int totalPage = moviePages.getTotalPages();

        List<MoviesResponse> moviesResponses = getMoviesPage(moviePages, movies);

        return AdminMoviesResponse.builder()
                .currentPage(pageNumber)
                .results(moviesResponses)
                .totalPages(totalPage)
                .build();
    }

    private List<MoviesResponse> getMoviesPage(Page<Movie> moviePage, List<Movie> movies) {

        if (moviePage.isEmpty()) {
            throw new MovieNotFoundException("검색 결과가 없습니다.");
        }

        return movies.stream()
                .map(movie -> {
                    List<MovieActor> movieActors = movieActorRepository.findMovieActorsByMovieId(movie.getId());
                    List<String> actorsName = movieActors.stream()
                            .map(movieActor -> movieActor.getActor().getName())
                            .toList();

                    String movieDirector = movieDirectorRepository.findMovieDirectorByMovieId(movie.getId())
                            .map(director -> director.getDirector().getName())
                            .orElse("N/A");

                    return MoviesResponse.builder()
                            .movieId(movie.getId())
                            .title(movie.getTitle())
                            .releaseDate(movie.getReleasedAt())
                            .plot(movie.getPlot())
                            .actors(actorsName)
                            .director(movieDirector)
                            .build();
                })
                .toList();
    }
}
