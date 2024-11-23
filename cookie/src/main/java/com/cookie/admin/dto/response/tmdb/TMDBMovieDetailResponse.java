package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TMDBMovieDetailResponse {

    private int id;
    private String title;
    private int runtime;
    private List<TMDBGenre> genres;
    private String overview;
    private boolean video;

    @JsonProperty("origin_country")
    private List<String> originCountry;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("release_date")
    private String releaseDate;
}
