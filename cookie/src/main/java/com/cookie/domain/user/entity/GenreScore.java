package com.cookie.domain.user.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenreScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private long romance;
    private long horror;
    private long comedy;
    private long action;
    private long fantasy;
    private long animation;
    private long crime;
    private long sf;
    private long music;
    private long thriller;
    private long war;
    private long documentary;
    private long drama;
    private long family;
    private long history;
    private long mistery;
    private long tvMovie;
    private long western;
    private long adventure;

    @Builder
    public GenreScore(User user, long romance, long horror, long comedy, long action, long fantasy, long animation, long crime, long sf, long music, long thriller, long war, long documentary, long drama, long family, long history, long mistery, long tvMovie, long western, long adventure) {
        this.user = user;
        this.romance = romance;
        this.horror = horror;
        this.comedy = comedy;
        this.action = action;
        this.fantasy = fantasy;
        this.animation = animation;
        this.crime = crime;
        this.sf = sf;
        this.music = music;
        this.thriller = thriller;
        this.war = war;
        this.documentary = documentary;
        this.drama = drama;
        this.family = family;
        this.history = history;
        this.mistery = mistery;
        this.tvMovie = tvMovie;
        this.western = western;
        this.adventure = adventure;
    }
}
