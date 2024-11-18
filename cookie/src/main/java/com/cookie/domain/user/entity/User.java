package com.cookie.domain.user.entity;

import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String nickname;
    private LocalDateTime birthdate;
    private String profileImage;
    private SocialProvider socialProvider;
    private String email;
    private Role role;
    private String socialId;

    @Builder
    public User(String username, String nickname, LocalDateTime birthdate, String profileImage, SocialProvider socialProvider, String email, Role role, String socialId) {
        this.username = username;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.profileImage = profileImage;
        this.socialProvider = socialProvider;
        this.email = email;
        this.role = role;
        this.socialId = socialId;
    }
}
