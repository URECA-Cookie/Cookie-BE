package com.cookie.admin.dto.response.tmdb;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TMDBCasts {

    private Long tmdbCasterId;
    private String name;
    private String profilePath;

    public TMDBCasts(Long tmdbCasterId, String name, String profilePath) {
        this.tmdbCasterId = tmdbCasterId;
        this.name = name;
        this.profilePath = profilePath;
    }
}
