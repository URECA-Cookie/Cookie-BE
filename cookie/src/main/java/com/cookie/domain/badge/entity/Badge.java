package com.cookie.domain.badge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String badgeImage;
    private String grade;
    private Long needPoint;

    @Builder
    public Badge(Long id, String name, String badgeImage, String grade, Long needPoint) {
        this.id = id;
        this.name = name;
        this.badgeImage = badgeImage;
        this.grade = grade;
        this.needPoint = needPoint;
    }

    public void updateBadgeName(String newName) {
        this.name = newName;
    }

    public void updateBadgeImage(String BadgeImage) {
        this.badgeImage = BadgeImage;
    }

    public void updateGrade(String newGrade) {
        this.grade = newGrade;
    }

    public void updateNeedPoint(Long newNeedPoint) {
        this.needPoint = newNeedPoint;
    }
}
