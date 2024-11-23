package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
public class TMDBMovieResponse {
    private Long id;
    private String title;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("poster_path")
    private String posterPath;

    public TMDBMovieResponse(Long id, String title, Integer voteCount, String posterPath) {
        this.id = id;
        this.title = title;
        this.voteCount = voteCount;
        this.posterPath = posterPath;
    }
}
