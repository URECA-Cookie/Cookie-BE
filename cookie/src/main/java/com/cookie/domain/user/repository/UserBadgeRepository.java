package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    // 유저 ID로 유저가 보유한 모든 뱃지 조회 (뱃지 정보 포함)
    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user.id = :userId")
    List<UserBadge> findAllByUserId(@Param("userId") Long userId);

    // 유저 ID로 대표 뱃지 조회
    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user.id = :userId AND ub.isMain = true")
    UserBadge findMainBadgeByUserId(@Param("userId") Long userId);

    Optional<UserBadge> findByUserIdAndIsMainTrue(Long userId);

    @Modifying
    @Query("""
        DELETE FROM UserBadge ub
        WHERE ub.badge.id = :badgeId
    """)
    void deleteUserBadgeByBadgeId(@Param("badgeId") Long badgeId);
}
