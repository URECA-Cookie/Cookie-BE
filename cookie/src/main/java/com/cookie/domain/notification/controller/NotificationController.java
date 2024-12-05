package com.cookie.domain.notification.controller;

import com.cookie.domain.notification.dto.request.FcmTokenRequest;
import com.cookie.domain.notification.service.FcmTokenService;
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

    @PostMapping("/token")
    public ApiSuccess<?> saveToken(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @RequestBody FcmTokenRequest fcmTokenRequest) {
        Long userId = customOAuth2User.getId();
        fcmTokenService.saveToken(userId, fcmTokenRequest.getToken());
        return ApiUtil.success("SUCCESS");
    }

}
