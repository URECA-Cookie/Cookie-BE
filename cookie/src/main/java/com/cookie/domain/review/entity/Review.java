package com.cookie.domain.review.entity;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.user.entity.User;
import com.cookie.global.entity.BaseTimeEntity;
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

    private Integer movieScore;
    private boolean isHide;
    private boolean isSpoiler;
    private long reviewLike;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> reviewComments = new ArrayList<>();

    @Builder
    public Review(Movie movie, User user, String content, Integer movieScore, boolean isHide, boolean isSpoiler, long reviewLike) {
        this.movie = movie;
        this.user = user;
        this.content = content;
        this.movieScore = movieScore;
        this.isHide = isHide;
        this.isSpoiler = isSpoiler;
        this.reviewLike = reviewLike;
    }

    public void update(String content, Integer movieScore, boolean isSpoiler) {
        this.content = content;
        this.movieScore = movieScore;
        this.isSpoiler = isSpoiler;
    }

    public void updateIsHide(boolean newHideStatus) {
        this.isHide = newHideStatus;
    }

    public void updateIsSpoiler(boolean newSpoilerStatus) {
        this.isSpoiler = newSpoilerStatus;
    }

    public void increaseLikeCount() {
        this.reviewLike++;
    }

    public void decreaseLikeCount() {
        if(this.reviewLike > 0) this.reviewLike--;
    }
}
