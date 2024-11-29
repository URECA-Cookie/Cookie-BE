package com.cookie.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadgeAccumulationPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private long romancePoint;
    private long horrorPoint;
    private long comedyPoint;
    private long actionPoint;
    private long fantasyPoint;
    private long animationPoint;
    private long crimePoint;
    private long sfPoint;
    private long musicPoint;
    private long thrillerPoint;
    private long warPoint;
    private long documentaryPoint;
    private long dramaPoint;
    private long familyPoint;
    private long historyPoint;
    private long misteryPoint;
    private long tvMoviePoint;
    private long westernPoint;
    private long adventurePoint;

    @Builder
    public BadgeAccumulationPoint(User user, long romancePoint, long horrorPoint, long comedyPoint, long actionPoint,
                                  long fantasyPoint, long animationPoint, long crimePoint, long sfPoint,
                                  long musicPoint, long thrillerPoint, long warPoint, long documentaryPoint,
                                  long dramaPoint, long familyPoint, long historyPoint, long misteryPoint,
                                  long tvMoviePoint, long westernPoint, long adventurePoint) {
        this.user = user;
        this.romancePoint = romancePoint;
        this.horrorPoint = horrorPoint;
        this.comedyPoint = comedyPoint;
        this.actionPoint = actionPoint;
        this.fantasyPoint = fantasyPoint;
        this.animationPoint = animationPoint;
        this.crimePoint = crimePoint;
        this.sfPoint = sfPoint;
        this.musicPoint = musicPoint;
        this.thrillerPoint = thrillerPoint;
        this.warPoint = warPoint;
        this.documentaryPoint = documentaryPoint;
        this.dramaPoint = dramaPoint;
        this.familyPoint = familyPoint;
        this.historyPoint = historyPoint;
        this.misteryPoint = misteryPoint;
        this.tvMoviePoint = tvMoviePoint;
        this.westernPoint = westernPoint;
        this.adventurePoint = adventurePoint;
    }
}
