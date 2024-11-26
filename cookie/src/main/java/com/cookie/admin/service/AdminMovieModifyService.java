package com.cookie.admin.service;

import com.cookie.admin.dto.response.AdminMovieCategoryResponse;
import com.cookie.admin.dto.response.MovieCategories;
import com.cookie.admin.exception.MovieNotFoundException;
import com.cookie.admin.repository.CategoryRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieCategory;
import com.cookie.domain.movie.repository.MovieActorRepository;
import com.cookie.domain.movie.repository.MovieCategoryRepository;
import com.cookie.domain.movie.repository.MovieImageRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMovieModifyService {

    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;

    private final MovieActorRepository movieActorRepository;
    private final MovieCategoryRepository movieCategoryRepository;
    private final MovieImageRepository movieImageRepository;

    @Transactional
    public AdminMovieCategoryResponse updateMovieCategory(Long movieId, List<MovieCategories> categories) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("해당 영화 정보가 존재하지 않습니다."));

        Set<Long> existingCategoryIds = movieCategoryRepository.findMovieCategoriesById(movieId).stream()
                .map(movieCategory -> movieCategory.getCategory().getId())
                .collect(Collectors.toSet());

        for (MovieCategories category : categories) {
            Long categoryId = category.getCategoryId();

            if (category.isConnect()) {
                if (!existingCategoryIds.contains(categoryId)) {
                    Category categoryEntity = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다."));

                    MovieCategory newCategory = MovieCategory.builder()
                            .movie(movie)
                            .category(categoryEntity)
                            .build();
                    movieCategoryRepository.save(newCategory);
                    existingCategoryIds.add(categoryId);
                }
            } else {
                if (existingCategoryIds.contains(categoryId)) {
                    movieCategoryRepository.deleteByMovieIdAndCategoryId(movieId, categoryId);
                    existingCategoryIds.remove(categoryId);
                }
            }
        }

        List<MovieCategories> movieCategories = categoryRepository.findAll().stream()
                .map(category -> MovieCategories.builder()
                        .categoryId(category.getId())
                        .mainCategory(category.getMainCategory())
                        .subCategory(category.getSubCategory())
                        .isConnect(existingCategoryIds.contains(category.getId()))
                        .build())
                .toList();

        return AdminMovieCategoryResponse.builder()
                .movieId(movieId)
                .title(movie.getTitle())
                .posterPath(movie.getPoster())
                .movieCategories(movieCategories)
                .build();
    }

    @Transactional
    public List<Long> deleteMovie(List<Long> movieId) {

        List<Movie> movies = movieRepository.findAllById(movieId);

        if (movies.isEmpty()) {
            throw new MovieNotFoundException("영화 id를 다시 한 번 확인해주세요.");
        }

        List<Long> deleteMovieIds = new ArrayList<>();

        try {
            for (Movie movie : movies) {

                movieActorRepository.deleteByMovieId(movie.getId());
                movieCategoryRepository.deleteByMovieId(movie.getId());
                movieImageRepository.deleteByMovieId(movie.getId());

                movieRepository.deleteByMovieId(movie.getId());

                deleteMovieIds.add(movie.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("영화 삭제 중 문제가 발생했습니다. 영화 ID: " + movieId, e);
        }

        return deleteMovieIds;
    }
}
