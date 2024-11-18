package com.cookie.domain.badge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private int needPoint;
    private int grade;

    @Builder
    public Badge(String name, String badgeImage, int needPoint, int grade) {
        this.name = name;
        this.badgeImage = badgeImage;
        this.needPoint = needPoint;
        this.grade = grade;
    }
}
