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

    private String genreCode;
    private double genreScore;

    @Builder
    public GenreScore(User user, String genreCode, double genreScore) {
        this.user = user;
        this.genreCode = genreCode;
        this.genreScore = genreScore;
    }
}
