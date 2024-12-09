package com.cookie.domain.notification.controller;

import com.cookie.domain.notification.dto.request.FcmTokenRequest;
import com.cookie.domain.notification.service.FcmTokenService;
import com.cookie.domain.notification.service.NotificationService;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification/")
public class NotificationController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/fcm-token")
    public ApiSuccess<?> saveFcmToken(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @RequestBody FcmTokenRequest fcmTokenRequest) {
        Long userId = customOAuth2User.getId();
        fcmTokenService.saveFcmToken(userId, fcmTokenRequest.getToken()); // 토큰 저장
        return ApiUtil.success("SUCCESS");
    }

    @DeleteMapping("/fcm-token")
    public ApiSuccess<?> deleteFcmToken(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = customOAuth2User.getId();
        fcmTokenService.deleteFcmToken(userId); // 토큰 삭제
        return ApiUtil.success("SUCCESS");
    }

}
