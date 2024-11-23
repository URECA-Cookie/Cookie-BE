package com.cookie.domain.movie.entity;

import com.cookie.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long TMDBMovieId;
    private String title;
    private String poster;
    @Lob
    private String plot;
    private String releasedAt;
    private Integer runtime;
    private double score;
    private String certification;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieImage> movieImages;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieVideo> movieVideos;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieCountry> movieCountries;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCategory> movieCategories = new ArrayList<>();

    @Builder
    public Movie(Long id, Long TMDBMovieId, String title, String poster, String plot,
                 String releasedAt, Integer runtime, double score, String certification) {
        this.id = id;
        this.TMDBMovieId = TMDBMovieId;
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.releasedAt = releasedAt;
        this.runtime = runtime;
        this.score = score;
        this.certification = certification;
    }
}
