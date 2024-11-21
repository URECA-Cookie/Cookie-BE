package com.cookie.domain.movie.entity;

import com.cookie.domain.movie.entity.enums.Rating;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    // 영화 이미지 연관 관계
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieImage> movieImages;

    // 영화 비디오 연관 관계
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieVideo> movieVideos;

    // 영화 국가 연관 관계
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieCountry> movieCountries;

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
