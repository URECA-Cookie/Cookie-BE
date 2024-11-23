package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);
    boolean existsBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);
}