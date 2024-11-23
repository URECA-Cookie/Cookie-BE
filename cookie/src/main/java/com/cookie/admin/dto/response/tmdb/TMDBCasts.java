package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TMDBCasts {

    private Long tmdbCasterId;
    private String name;
    private String profilePath;

    @JsonCreator
    public TMDBCasts(
            @JsonProperty("tmdbCasterId") Long tmdbCasterId,
            @JsonProperty("name") String name,
            @JsonProperty("profilePath") String profilePath) {
        this.tmdbCasterId = tmdbCasterId;
        this.name = name;
        this.profilePath = profilePath;
    }
}


