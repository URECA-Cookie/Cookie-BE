package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminMovieBaseAddResponse {

    private Long movieAddCount;

    public AdminMovieBaseAddResponse(Long movieAddCount) {
        this.movieAddCount = movieAddCount;
    }
}
