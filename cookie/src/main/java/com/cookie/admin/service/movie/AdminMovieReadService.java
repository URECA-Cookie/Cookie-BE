package com.cookie.admin.service.movie;

import com.cookie.admin.dto.response.AdminMovieCategoryResponse;
import com.cookie.admin.dto.response.AdminMovieDetailResponse;
import com.cookie.admin.dto.response.MovieCasts;
import com.cookie.admin.dto.response.MovieCategories;
import com.cookie.admin.exception.MovieNotFoundException;
import com.cookie.admin.repository.CategoryRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.director.entity.Director;
import com.cookie.domain.director.repository.DirectorRepository;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieActor;
import com.cookie.domain.movie.entity.MovieImage;
import com.cookie.domain.movie.repository.MovieActorRepository;
import com.cookie.domain.movie.repository.MovieCategoryRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMovieReadService {

    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final MovieCategoryRepository movieCategoryRepository;
    private final DirectorRepository directorRepository;
    private final MovieActorRepository movieActorRepository;

    @Transactional(readOnly = true)
    public AdminMovieCategoryResponse getMovieCategory(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("해당 영화 정보가 존재하지 않습니다."));

        List<Long> movieCategoryIds = movieCategoryRepository.findCategoriesById(movieId);
        List<Category> categories = categoryRepository.findAll();

        List<MovieCategories> movieCategories = categories.stream()
                .filter(category -> !category.getSubCategory().equals("N/A"))
                .map(data -> {
                    Long id = data.getId();
                    boolean isConnect = movieCategoryIds.contains(id);

                    return MovieCategories.builder()
                            .categoryId(id)
                            .mainCategory(data.getMainCategory())
                            .subCategory(data.getSubCategory())
                            .isConnect(isConnect)
                            .build();
                })
                .toList();

        return AdminMovieCategoryResponse.builder()
                .movieId(movieId)
                .title(movie.getTitle())
                .posterPath(movie.getPoster())
                .movieCategories(movieCategories)
                .build();
    }

    @Transactional(readOnly = true)
    public AdminMovieDetailResponse getMovieDetail(Long movieId) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("해당 영화 정보가 존재하지 않습니다."));

        Director director = directorRepository.findById(movie.getDirector().getId())
                .orElseThrow(() -> new IllegalArgumentException("감독 정보가 없습니다."));

        MovieCasts movieDirector = MovieCasts.builder()
                .casterId(director.getId())
                .name(director.getName())
                .profilePath(director.getProfileImage())
                .build();

        List<MovieActor> movieActors = movieActorRepository.findMovieActorsByMovieId(movieId);

        List<MovieCasts> actors = movieActors.stream()
                .map(actor -> MovieCasts.builder()
                        .casterId(actor.getId())
                        .name(actor.getActor().getName())
                        .profilePath(actor.getActor().getProfileImage())
                        .build())
                .toList();

        String imageUrl = "https://image.tmdb.org/t/p/w500/";

        return AdminMovieDetailResponse.builder()
                .movieId(movieId)
                .title(movie.getTitle())
                .director(movieDirector)
                .runtime(movie.getRuntime())
                .posterPath(imageUrl + movie.getPoster())
                .releaseDate(movie.getReleasedAt())
                .certification(movie.getCertification())
                .country(movie.getCountry().getName())
                .plot(movie.getPlot())
                .youtube(movie.getYoutubeUrl())
                .stillCuts(movie.getMovieImages().stream().map(MovieImage::getUrl).toList())
                .actors(actors)
                .categories(movie.getMovieCategories().stream().map(data -> data.getCategory().getSubCategory()).toList())
                .build();
    }
}
