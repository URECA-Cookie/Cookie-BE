package com.cookie.domain.matchup.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchup_id")
    private MatchUp matchUp;

    private long touching;
    private long angry;
    private long joy;
    private long immersion;
    private long excited;
    private long empathy;
    private long tension;

    @Builder
    public EmotionPoint(MatchUp matchUp, long touching, long angry, long joy, long immersion, long excited, long empathy, long tension) {
        this.matchUp = matchUp;
        this.touching = touching;
        this.angry = angry;
        this.joy = joy;
        this.immersion = immersion;
        this.excited = excited;
        this.empathy = empathy;
        this.tension = tension;
    }
}
