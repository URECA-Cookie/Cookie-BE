package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class TMDBMovieImage {

    @JsonProperty("file_path")
    private String filePath;

    public TMDBMovieImage(String filePath) {
        this.filePath = filePath;
    }
}
