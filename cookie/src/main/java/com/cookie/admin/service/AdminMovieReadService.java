package com.cookie.admin.service;

import com.cookie.admin.dto.response.AdminMovieCategoryResponse;
import com.cookie.admin.dto.response.MovieCategories;
import com.cookie.admin.exception.MovieNotFoundException;
import com.cookie.admin.repository.CategoryRepository;
import com.cookie.admin.repository.MovieCategoryRepository;
import com.cookie.admin.repository.MovieRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.movie.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMovieReadService {

    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final MovieCategoryRepository movieCategoryRepository;

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
}
