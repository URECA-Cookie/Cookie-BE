package com.cookie.domain.matchup.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchUpMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String movieTitle;
    private String moviePoster;
    private long voteCount;
    private boolean win;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "charm_point_id")
    private CharmPoint charmPoint;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = " emotion_point_id")
    private EmotionPoint emotionPoint;

    public void incrementVoteCount() {
        this.voteCount++;
    }
    public void changeWinStatus(boolean winStatus) {
        this.win = winStatus;
    }

    @Builder
    public MatchUpMovie(String movieTitle, String moviePoster, long voteCount, boolean win, CharmPoint charmPoint, EmotionPoint emotionPoint) {
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.voteCount = voteCount;
        this.win = win;
        this.charmPoint = charmPoint;
        this.emotionPoint = emotionPoint;
    }

    public void updateMatchMovie(String newMovieTitle, String newMoviePoster) {
        this.movieTitle = newMovieTitle;
        this.moviePoster = newMoviePoster;
    }
}
