package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TMDBMovieCreditsResponse {

    @JsonProperty("id")
    private Long tmdbCasterId;
    @JsonProperty("known_for_department")
    private String knownForDepartment;
    @JsonProperty("name")
    private String name;
    private Double popularity;
    private String job;
    @JsonProperty("profile_path")
    private String profilePath;

    @JsonCreator
    public TMDBMovieCreditsResponse(
            @JsonProperty("id") Long tmdbCasterId,
            @JsonProperty("known_for_department") String knownForDepartment,
            @JsonProperty("name") String name,
            @JsonProperty("popularity") Double popularity,
            @JsonProperty("job") String job,
            @JsonProperty("profile_path") String profilePath) {
        this.tmdbCasterId = tmdbCasterId;
        this.knownForDepartment = knownForDepartment;
        this.name = name;
        this.popularity = popularity;
        this.job = job;
        this.profilePath = profilePath;
    }
}

