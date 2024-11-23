package com.cookie.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOfMovieResponse {
    private String title;
    private String poster;
    private String certification;
    private int runtime;
    private List<String> subCategories;
    private List<String> countries;
    private String releasedAt;
    private List<MovieReviewResponse> reviews;
}
