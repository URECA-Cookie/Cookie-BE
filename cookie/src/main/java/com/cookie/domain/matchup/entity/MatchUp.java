package com.cookie.domain.matchup.entity;

import com.cookie.domain.chat.entity.Chatroom;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import com.cookie.domain.matchup.entity.enums.MatchUpType;
import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchUp extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private MatchUpType type;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String movieTitle;
    private String moviePoster;
    private long movieLike;
    private boolean isWin;
    @Enumerated(EnumType.STRING)
    private MatchUpStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @Builder
    public MatchUp(String title, MatchUpType type, LocalDateTime startAt, LocalDateTime endAt, String movieTitle, String moviePoster, long movieLike, boolean isWin, MatchUpStatus status, Chatroom chatroom) {
        this.title = title;
        this.type = type;
        this.startAt = startAt;
        this.endAt = endAt;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.movieLike = movieLike;
        this.isWin = isWin;
        this.status = status;
        this.chatroom = chatroom;
    }
}