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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "movie1_id")
    private MatchUpMovie movie1;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "movie2_id")
    private MatchUpMovie movie2;

    @Enumerated(EnumType.STRING)
    private MatchUpStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @Builder
    public MatchUp(String title, MatchUpType type, LocalDateTime startAt, LocalDateTime endAt, MatchUpMovie movie1, MatchUpMovie movie2, MatchUpStatus status, Chatroom chatroom) {
        this.title = title;
        this.type = type;
        this.startAt = startAt;
        this.endAt = endAt;
        this.movie1 = movie1;
        this.movie2 = movie2;
        this.status = status;
        this.chatroom = chatroom;
    }

    public void changeStatus(MatchUpStatus newStatus) {
        this.status = newStatus;
    }

    public void updateWinner() {
        if (this.movie1.getVoteCount() > this.movie2.getVoteCount()) {
            this.movie1.changeWinStatus(true);
        } else if (this.movie1.getVoteCount() < this.movie2.getVoteCount()) {
            this.movie2.changeWinStatus(true);
        } else {
            this.movie1.changeWinStatus(false);
            this.movie2.changeWinStatus(false);
        }
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateType(MatchUpType newType) {
        this.type = newType;
    }

    public void updateStartAt(LocalDateTime newStartAt) {
        this.startAt = newStartAt;
    }

    public void updateEndAt(LocalDateTime newEndAt) {
        this.endAt = newEndAt;
    }

}
