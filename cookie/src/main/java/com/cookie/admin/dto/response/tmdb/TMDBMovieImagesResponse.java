package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TMDBMovieImagesResponse {

    @JsonProperty("backdrops")
    private List<TMDBMovieImage> backdrops;
}
