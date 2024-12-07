package com.cookie.domain.user.entity;

import com.cookie.domain.badge.entity.Badge;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.movie.entity.MovieLike;
import com.cookie.domain.notification.entity.FcmToken;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.entity.ReviewComment;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

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
    private boolean isPushEnabled;
    private boolean isEmailEnabled;
    private String password;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBadge> userBadges = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BadgeAccumulationPoint> badgeAccumulationPoints = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GenreScore> genreScores = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyGenreScore> dailyGenreScores = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieLike> movieLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> reviewComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchUpParticipation> matchUpParticipations = new ArrayList<>();


    // mainBadge 반환 메서드
    public Badge getMainBadge() {
        return userBadges.stream()
                .filter(UserBadge::isMain)
                .map(UserBadge::getBadge)
                .findFirst()
                .orElse(null);
    }

    @Builder
    public User(String nickname, String profileImage, SocialProvider socialProvider, String email, Role role, String socialId, boolean isPushEnabled, boolean isEmailEnabled, String password, Category category) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.socialProvider = socialProvider;
        this.email = email;
        this.role = role;
        this.socialId = socialId;
        this.isPushEnabled = isPushEnabled;
        this.isEmailEnabled = isEmailEnabled;
        this.password = password;
        this.category = category;
    }


    public void updateProfile(String profileImage, String nickname, Category genreId) {
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
        this.nickname = nickname;
        this.category = genreId;
    }

}

