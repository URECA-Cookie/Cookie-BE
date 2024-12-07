package com.cookie.domain.badge.repository;

import com.cookie.domain.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    @Query("""
        SELECT b
        FROM Badge b
        WHERE b.name = :name
    """)
    Optional<Badge> findBadgeByName(@Param("name") String name);
}
