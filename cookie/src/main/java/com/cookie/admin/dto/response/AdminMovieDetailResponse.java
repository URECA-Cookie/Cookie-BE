package com.cookie.admin.dto.response;

import com.cookie.admin.dto.response.tmdb.TMDBCasts;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class AdminMovieDetailResponse {
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

    public AdminMovieDetailResponse(Long movieId, String title, TMDBCasts director, Integer runtime, String posterPath, String releaseDate, String certification,
                                    String country, String plot, String youtube, List<String> stillCuts, List<TMDBCasts> actors, List<String> categories) {
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
