package com.cookie.domain.user.entity;


import com.cookie.domain.badge.entity.Badge;
import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBadge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean isMain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @Builder
    public UserBadge(boolean isMain, User user, Badge badge) {
        this.isMain = isMain;
        this.user = user;
        this.badge = badge;
    }
}
