package com.cookie.domain.user.controller;

import com.cookie.domain.user.dto.request.auth.AdminLoginRequest;
import com.cookie.domain.user.dto.request.auth.AdminRegisterRequest;
import com.cookie.domain.user.dto.request.auth.RegisterRequest;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.jwt.JWTUtil;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest registerRequest) {

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

        String token = jwtUtil.createJwt(newUser.getNickname(), newUser.getRole().name());

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

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRegisterRequest request) {
        if (userService.isDuplicateNickname(request.getId())) {
            return ResponseEntity.badRequest().body(ApiUtil.error(400, "DUPLICATED_ID"));
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User admin = User.builder()
                .nickname(request.getId())
                .password(encodedPassword)
                .role(Role.ADMIN)
                .build();

        userService.saveUser(admin);

        return ResponseEntity.ok(ApiUtil.success("SUCCESS"));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword()));

            String token = jwtUtil.createJwt(
                    authentication.getName(),
                    authentication.getAuthorities().iterator().next().getAuthority()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(ApiUtil.success("SUCCESS"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(ApiUtil.error(401, "INVALID_CREDENTIALS"));
        }
    }

}