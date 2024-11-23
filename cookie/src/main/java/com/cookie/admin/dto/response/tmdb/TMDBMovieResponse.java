package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TMDBMovieResponse {
    private Long id;
    private String title;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonCreator
    public TMDBMovieResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("vote_count") Integer voteCount,
            @JsonProperty("poster_path") String posterPath) {
        this.id = id;
        this.title = title;
        this.voteCount = voteCount;
        this.posterPath = posterPath;
    }
}
