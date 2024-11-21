package com.cookie.domain.user.controller;

import com.cookie.domain.user.dto.request.RegisterRequest;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.repository.UserRepository;
import com.cookie.global.jwt.JWTUtil;
import com.cookie.global.util.ApiUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {

        if (userRepository.existsByNickname(registerRequest.getNickname())) {
            return ResponseEntity.badRequest().body(ApiUtil.error(400, "DUPLICATED_NICKNAME"));
        }

        User newUser = User.builder()
                .socialProvider(registerRequest.getSocialProvider())
                .socialId(registerRequest.getSocialId())
                .email(registerRequest.getEmail())
                .nickname(registerRequest.getNickname())
                .profileImage(registerRequest.getProfileImage())
                .role(Role.USER)
                .build();

        userRepository.save(newUser);

        String token = jwtUtil.createJwt(newUser.getNickname(), newUser.getRole().name(), 60 * 60 * 60L);
        response.addCookie(createCookie("Authorization", token));

        return ResponseEntity.ok(ApiUtil.success("SUCCESS"));
    }

    @GetMapping("/retrieve-token")
    public ResponseEntity<?> retrieveToken(@CookieValue(name = "Authorization", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "Invalid or missing token"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        return ResponseEntity.ok().headers(headers).build();
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}