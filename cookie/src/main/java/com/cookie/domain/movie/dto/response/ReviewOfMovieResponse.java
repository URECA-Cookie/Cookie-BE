package com.cookie.domain.movie.dto.response;

import com.cookie.domain.review.dto.response.MovieReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOfMovieResponse {
    private String title;
    private String poster;
    private String rating;
    private int runtime;
    private List<String> subCategories;
    private List<String> countries;
    private LocalDateTime releasedAt;
    private List<MovieReviewResponse> reviews;
}
