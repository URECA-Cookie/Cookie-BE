package com.cookie.domain.notification.repository;

import com.cookie.domain.notification.entity.FcmToken;
import com.cookie.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
}
