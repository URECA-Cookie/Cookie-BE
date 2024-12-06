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

    private Long accPoint;

    @Builder
    public BadgeAccumulationPoint(Long id, User user, Long accPoint) {
        this.id = id;
        this.user = user;
        this.accPoint = accPoint;
    }

    public void updateAccPoint(Long newAccPoint) {
        this.accPoint = newAccPoint;
    }
}
