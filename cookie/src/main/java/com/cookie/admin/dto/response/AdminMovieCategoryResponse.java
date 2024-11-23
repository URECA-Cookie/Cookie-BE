package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMovieCategoryResponse {

    private Long movieId;
    private String title;
    private String posterPath;
    private List<MovieCategories> movieCategories;

    public AdminMovieCategoryResponse(Long movieId, String title, String posterPath, List<MovieCategories> movieCategories) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.movieCategories = movieCategories;
    }
}
