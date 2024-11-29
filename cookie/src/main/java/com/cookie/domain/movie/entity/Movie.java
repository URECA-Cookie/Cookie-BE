package com.cookie.domain.movie.entity;

import com.cookie.domain.country.entity.Country;
import com.cookie.domain.director.entity.Director;
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
    private String youtubeUrl;
    @Lob
    private String plot;
    private String releasedAt;
    private Integer runtime;
    private double score;
    private String certification;
    private Long movieLikes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id")
    private Director director;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieImage> movieImages = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCategory> movieCategories = new ArrayList<>();

    @Builder
    public Movie(Long id, Long TMDBMovieId, String title, String poster, String youtubeUrl, String plot, String releasedAt, Integer runtime,
                 double score, String certification, Director director, Country country,
                 List<MovieImage> movieImages, List<Review> reviews, List<MovieCategory> movieCategories) {
        this.id = id;
        this.TMDBMovieId = TMDBMovieId;
        this.title = title;
        this.poster = poster;
        this.youtubeUrl = youtubeUrl;
        this.plot = plot;
        this.releasedAt = releasedAt;
        this.runtime = runtime;
        this.score = score;
        this.certification = certification;
        this.director = director;
        this.country = country;
        this.movieImages = movieImages;
        this.reviews = reviews;
        this.movieCategories = movieCategories;
    }

    public void updateScore(Double score) {
        this.score = score;
    }

}