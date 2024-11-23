package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TMDBMovieImagesResponse {

    @JsonProperty("backdrops")
    private List<TMDBMovieImage> backdrops;
}
