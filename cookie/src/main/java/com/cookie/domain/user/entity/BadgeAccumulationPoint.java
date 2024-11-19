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
    @JoinColumn(name = "user_id")
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
    private long queerPoint;
    private long warPoint;
    private long documentaryPoint;

    @Builder
    public BadgeAccumulationPoint(User user, long romancePoint, long horrorPoint, long comedyPoint, long actionPoint, long fantasyPoint, long animationPoint, long crimePoint, long sfPoint, long musicPoint, long thrillerPoint, long queerPoint, long warPoint, long documentaryPoint) {
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
        this.queerPoint = queerPoint;
        this.warPoint = warPoint;
        this.documentaryPoint = documentaryPoint;
    }
}
