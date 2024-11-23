package com.cookie.admin.service;

import com.cookie.admin.dto.response.AdminMovieCategoryResponse;
import com.cookie.admin.dto.response.AdminMovieDeleteResponse;
import com.cookie.admin.dto.response.MovieCategories;
import com.cookie.admin.exception.MovieNotFoundException;
import com.cookie.admin.repository.CategoryRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieCategory;
import com.cookie.domain.movie.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MovieCountryRepository movieCountryRepository;
    private final MovieDirectorRepository movieDirectorRepository;
    private final MovieImageRepository movieImageRepository;
    private final MovieVideoRepository movieVideoRepository;

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
    public AdminMovieDeleteResponse deleteMovie(Long movieId) {

        movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("해당 영화 정보가 존재하지 않습니다."));

        try {
            movieActorRepository.deleteByMovieId(movieId);
            movieCategoryRepository.deleteByMovieId(movieId);
            movieCountryRepository.deleteByMovieId(movieId);
            movieDirectorRepository.deleteByMovieId(movieId);
            movieImageRepository.deleteByMovieId(movieId);
            movieVideoRepository.deleteByMovieId(movieId);
            // 리뷰 (리뷰 좋아요, 리뷰 댓글), 영화 좋아요, 레파지토리 바탕으로 추후에 전부 삭제 기능 개발 필요

            movieRepository.deleteByMovieId(movieId);

            return AdminMovieDeleteResponse.builder()
                    .movieId(movieId)
                    .message(movieId + " 번 영화가 정상적으로 삭제되었습니다.")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("영화 삭제 중 문제가 발생했습니다. 영화 ID: " + movieId, e);
        }
    }
}
