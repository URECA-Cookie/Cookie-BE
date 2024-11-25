package com.cookie.domain.user.controller;

import com.cookie.domain.category.service.CategoryService;
import com.cookie.domain.user.dto.request.auth.AdminLoginRequest;
import com.cookie.domain.user.dto.request.auth.AdminRegisterRequest;
import com.cookie.domain.user.dto.request.auth.RegisterRequest;
import com.cookie.domain.user.dto.response.auth.TokenResponse;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.jwt.JWTUtil;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CategoryService categoryService;

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
                .pushEnabled(registerRequest.isPushEnabled())
                .emailEnabled(registerRequest.isEmailEnabled())
                .category(categoryService.getCategoryById(registerRequest.getGenreId()))
                .role(Role.USER)
                .build();

        userService.registerUser(newUser);

        String accessToken = jwtUtil.createAccessToken(newUser.getNickname(), newUser.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(newUser.getNickname(), newUser.getRole().name());

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_ACCESS_TOKEN"));
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_REFRESH_TOKEN"));
        }

        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_ACCESS_TOKEN"));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_REFRESH_TOKEN"));
        }

        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok()
                .body(ApiUtil.success(response));
    }

    @GetMapping("/retrieve-token")
    public ResponseEntity<?> retrieveToken(@CookieValue(name = "Authorization", required = false) String accessToken,
                                           @CookieValue(name = "RefreshToken", required = false) String refreshToken) {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_ACCESS_TOKEN"));
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_REFRESH_TOKEN"));
        }

        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_ACCESS_TOKEN"));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_REFRESH_TOKEN"));
        }

        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok()
                .body(ApiUtil.success(response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_TOKEN"));
        }

        String refreshToken = refreshTokenHeader.substring(7);

        if (jwtUtil.isExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "TOKEN_EXPIRED"));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_TOKEN"));
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(username, role);
        TokenResponse response = TokenResponse.builder().accessToken(newAccessToken).build();

        return ResponseEntity.ok()
                .body(ApiUtil.success(response));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> validateNickname(@RequestParam("nickname") String nickname) {
        if (userService.isDuplicateNickname(nickname)) {
            return ResponseEntity.ok().body(ApiUtil.success("DUPLICATED_NICKNAME"));
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

        userService.registerAdmin(admin);

        return ResponseEntity.ok(ApiUtil.success("SUCCESS"));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword()));

            String accessToken = jwtUtil.createAccessToken(
                    authentication.getName(),
                    authentication.getAuthorities().iterator().next().getAuthority()
            );

            String refreshToken = jwtUtil.createRefreshToken(
                    authentication.getName(),
                    authentication.getAuthorities().iterator().next().getAuthority()
            );

            TokenResponse response = TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseEntity.ok()
                    .body(ApiUtil.success(response));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(ApiUtil.error(401, "INVALID_CREDENTIALS"));
        }
    }

}