package com.cookie.admin.dto.response.tmdb;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public TMDBMovieDetailResponse(
            @JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("runtime") int runtime,
            @JsonProperty("genres") List<TMDBGenre> genres,
            @JsonProperty("overview") String overview,
            @JsonProperty("video") boolean video,
            @JsonProperty("origin_country") List<String> originCountry,
            @JsonProperty("poster_path") String posterPath,
            @JsonProperty("release_date") String releaseDate) {
        this.id = id;
        this.title = title;
        this.runtime = runtime;
        this.genres = genres;
        this.overview = overview;
        this.video = video;
        this.originCountry = originCountry;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
    }
}
