package com.cookie.admin.dto.response.tmdb;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TMDBMovieVideoResponse {

    private String key;
    private String type;
}
