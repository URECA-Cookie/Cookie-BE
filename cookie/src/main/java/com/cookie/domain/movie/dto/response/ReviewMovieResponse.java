package com.cookie.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMovieResponse {
    private Long movieId;
    private String title;
    private String poster;
}
