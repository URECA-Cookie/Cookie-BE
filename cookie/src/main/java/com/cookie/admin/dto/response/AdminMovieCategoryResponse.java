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
}
