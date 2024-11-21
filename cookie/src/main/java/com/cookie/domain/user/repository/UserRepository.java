package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findBySocialProviderAndSocialId(SocialProvider socialProvider, String socialId);
    boolean existsByNickname(String nickname);
}