package com.cookie.domain.user.entity;

import com.cookie.domain.user.entity.enums.ActionType;
import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyGenreScore extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String category;
    private long score;
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Builder
    public DailyGenreScore(User user, String category, long score, ActionType actionType) {
        this.user = user;
        this.category = category;
        this.score = score;
        this.actionType = actionType;
    }
}

