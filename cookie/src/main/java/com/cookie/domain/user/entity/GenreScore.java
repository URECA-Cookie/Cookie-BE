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

    private double romance;
    private double horror;
    private double comedy;
    private double action;
    private double fantasy;
    private double animation;
    private double crime;
    private double sf;
    private double music;
    private double thriller;
    private double queer;
    private double war;
    private double documentary;

    @Builder
    public GenreScore(User user, double romance, double horror, double comedy, double action, double fantasy, double animation, double crime, double sf, double music, double thriller, double queer, double war, double documentary) {
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
        this.queer = queer;
        this.war = war;
        this.documentary = documentary;
    }
}
