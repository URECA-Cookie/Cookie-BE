package com.cookie.domain.notification.service;

import com.cookie.domain.notification.entity.FcmToken;
import com.cookie.domain.notification.repository.FcmTokenRepository;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public void saveToken(Long userId, String token) {
        log.info("saveToken userId: {} and token: {}", userId, token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));

        FcmToken fcmToken = FcmToken.builder()
                .token(token)
                .user(user)
                .build();

        fcmTokenRepository.save(fcmToken);

    }

}
