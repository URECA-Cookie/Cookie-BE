package com.cookie.domain.movie.entity;

import com.cookie.domain.movie.entity.enums.Rating;
import com.cookie.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String poster;
    @Lob
    private String plot;
    private String company;
    private LocalDateTime releasedAt;
    private int runtime;
    private double score;
    @Enumerated(EnumType.STRING)
    private Rating rating;

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

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCountry> movieCountries = new ArrayList<>();

    @Builder
    public Movie(String title, String poster, String plot, String company, LocalDateTime releasedAt, int runtime, double score, Rating rating) {
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.company = company;
        this.releasedAt = releasedAt;
        this.runtime = runtime;
        this.score = score;
        this.rating = rating;
    }
}
