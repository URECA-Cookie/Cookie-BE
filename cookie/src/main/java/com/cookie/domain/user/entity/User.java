package com.cookie.domain.user.entity;

import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String socialId;

    @Builder
    public User(String nickname, String profileImage, SocialProvider socialProvider, String email, Role role, String socialId) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.socialProvider = socialProvider;
        this.email = email;
        this.role = role;
        this.socialId = socialId;
    }
}
