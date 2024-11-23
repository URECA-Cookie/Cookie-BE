package com.cookie.domain.user.controller;

import com.cookie.domain.user.dto.request.RegisterRequest;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.jwt.JWTUtil;
import com.cookie.global.util.ApiUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Value("${app.client.url}")
    private String clientUrl;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) throws IOException {

        if (userService.isDuplicateSocial(registerRequest.getSocialProvider(), registerRequest.getSocialId())) {
            return ResponseEntity.badRequest().body(ApiUtil.error(409, "ALREADY_REGISTERED"));
        }

        if (userService.isDuplicateNickname(registerRequest.getNickname())) {
            return ResponseEntity.badRequest().body(ApiUtil.error(400, "DUPLICATED_NICKNAME"));
        }

        User newUser = User.builder()
                .socialProvider(registerRequest.getSocialProvider())
                .socialId(registerRequest.getSocialId())
                .email(registerRequest.getEmail())
                .nickname(registerRequest.getNickname())
                .profileImage(registerRequest.getProfileImage())
                .isPushEnabled(registerRequest.isPushEnabled())
                .isEmailEnabled(registerRequest.isEmailEnabled())
                .role(Role.USER)
                .build();

        userService.saveUser(newUser);

        String token = jwtUtil.createJwt(newUser.getNickname(), newUser.getRole().name(), 60 * 60 * 60L);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiUtil.success("SUCCESS"));
    }

    @GetMapping("/retrieve-token")
    public ResponseEntity<?> retrieveToken(@CookieValue(name = "Authorization", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_TOKEN"));
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_TOKEN"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiUtil.success("SUCCESS"));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> validateNickname(String nickname) {
        if (userService.isDuplicateNickname(nickname)) {
            return ResponseEntity.badRequest().body(ApiUtil.error(400, "DUPLICATED_NICKNAME"));
        }

        return ResponseEntity.ok(ApiUtil.success("SUCCESS"));
    }

}