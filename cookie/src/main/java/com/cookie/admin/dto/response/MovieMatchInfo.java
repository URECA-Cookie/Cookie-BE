package com.cookie.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieMatchInfo {

    private String poster;
    private String movieTitle;

    @JsonCreator
    public MovieMatchInfo(
            @JsonProperty("poster") String poster,
            @JsonProperty("movieTitle") String movieTitle) {
        this.poster = poster;
        this.movieTitle = movieTitle;
    }
}
