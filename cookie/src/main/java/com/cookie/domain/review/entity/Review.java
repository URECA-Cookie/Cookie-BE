package com.cookie.domain.review.entity;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.user.entity.User;
import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    private String content;

    private double movieScore;
    private boolean isHide;
    private boolean isSpoiler;
    private long reviewLike;

    @Builder
    public Review(Movie movie, User user, String content, double movieScore, boolean isHide, boolean isSpoiler, long reviewLike) {
        this.movie = movie;
        this.user = user;
        this.content = content;
        this.movieScore = movieScore;
        this.isHide = isHide;
        this.isSpoiler = isSpoiler;
        this.reviewLike = reviewLike;
    }
}
