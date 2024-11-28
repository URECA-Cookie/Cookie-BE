package com.cookie.admin.dto.response;

import com.cookie.admin.dto.response.tmdb.TMDBCasts;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMovieTMDBDetailResponse {
    private Long movieId;
    private String title;
    private TMDBCasts director;
    private Integer runtime;
    private String posterPath;
    private String releaseDate;
    private String certification;
    private String country;
    private String plot;
    private String youtube;
    private List<String> stillCuts;
    private List<TMDBCasts> actors;
    private List<String> categories;

    @JsonCreator
    public AdminMovieTMDBDetailResponse(
            @JsonProperty("movieId") Long movieId,
            @JsonProperty("title") String title,
            @JsonProperty("director") TMDBCasts director,
            @JsonProperty("runtime") Integer runtime,
            @JsonProperty("posterPath") String posterPath,
            @JsonProperty("releaseDate") String releaseDate,
            @JsonProperty("certification") String certification,
            @JsonProperty("country") String country,
            @JsonProperty("plot") String plot,
            @JsonProperty("youtube") String youtube,
            @JsonProperty("stillCuts") List<String> stillCuts,
            @JsonProperty("actors") List<TMDBCasts> actors,
            @JsonProperty("categories") List<String> categories) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.runtime = runtime;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.certification = certification;
        this.country = country;
        this.plot = plot;
        this.youtube = youtube;
        this.stillCuts = stillCuts;
        this.actors = actors;
        this.categories = categories;
    }
}
