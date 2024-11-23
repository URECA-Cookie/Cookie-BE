package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AdminSearchResponse {

    private Long movieId;
    private String title;
    private String posterPath;

    public AdminSearchResponse(Long movieId, String title, String posterPath) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
    }
}
