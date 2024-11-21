package com.cookie.domain.user.entity;

import com.cookie.domain.badge.entity.Badge;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBadge> userBadges = new ArrayList<>();

    // mainBadge 반환 메서드
    public Badge getMainBadge() {
        return userBadges.stream()
                .filter(UserBadge::isMain)
                .map(UserBadge::getBadge)
                .findFirst()
                .orElse(null);
    }

    @Builder
    public User(String nickname, String profileImage, SocialProvider socialProvider, String email, Role role, String socialId) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.socialProvider = socialProvider;
        this.email = email;
        this.role = role;
        this.socialId = socialId;
    }

    public void updateProfile(String profileImage, String nickname) {
        this.profileImage = profileImage;
        this.nickname = nickname;
    }


}
