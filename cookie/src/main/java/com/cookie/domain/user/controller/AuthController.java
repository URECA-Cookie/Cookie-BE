package com.cookie.domain.user.controller;

import com.cookie.domain.category.service.CategoryService;
import com.cookie.domain.notification.service.FcmTokenService;
import com.cookie.domain.notification.service.NotificationService;
import com.cookie.domain.user.dto.request.auth.AdminLoginRequest;
import com.cookie.domain.user.dto.request.auth.AdminRegisterRequest;
import com.cookie.domain.user.dto.response.UserInfoResponse;
import com.cookie.domain.user.dto.response.UserResponse;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.domain.user.dto.response.auth.CustomUserDetails;
import com.cookie.domain.user.dto.response.auth.TokenResponse;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.Role;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.domain.user.service.UserService;
import com.cookie.global.jwt.JWTUtil;
import com.cookie.global.service.AWSS3Service;
import com.cookie.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "인증", description = "인증 API")
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
    private final AWSS3Service awss3Service;
    private final FcmTokenService fcmTokenService;
    private final NotificationService notificationService;

    @Operation(summary = "회원 가입", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(
            @RequestPart("socialProvider") String socialProvider,
            @RequestPart("socialId") String socialId,
            @RequestPart("email") String email,
            @RequestPart("nickname") String nickname,
            @RequestPart("profileImage") MultipartFile profileImage,
            @RequestPart("pushEnabled") String pushEnabled,
            @RequestPart("emailEnabled") String emailEnabled,
            @RequestPart("genreId") String genreId,
            @RequestPart("fcmToken") String fcmToken) {

        log.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", socialProvider, socialId, email, nickname, profileImage, pushEnabled, emailEnabled, genreId, fcmToken);
        boolean pushEnabledValue = Boolean.parseBoolean(pushEnabled);
        boolean emailEnabledValue = Boolean.parseBoolean(emailEnabled);
        Long genreIdValue = Long.parseLong(genreId);

        // 소셜 아이디 중복 체크
        if (userService.isDuplicateSocial(SocialProvider.valueOf(socialProvider.toUpperCase()), socialId)) {
            return ResponseEntity.badRequest().body(ApiUtil.error(409, "ALREADY_REGISTERED"));
        }

        // 닉네임 중복 체크
        if (userService.isDuplicateNicknameRegister(nickname)) {
            return ResponseEntity.badRequest().body(ApiUtil.error(400, "DUPLICATED_NICKNAME"));
        }

        String profileImageUrl = awss3Service.uploadImage(profileImage);

        // 새로운 사용자 등록
        User newUser = User.builder()
                .socialProvider(SocialProvider.valueOf(socialProvider.toUpperCase()))
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImageUrl)
                .isPushEnabled(pushEnabledValue)
                .isEmailEnabled(emailEnabledValue)
                .category(categoryService.getCategoryById(genreIdValue))
                .role(Role.USER)
                .build();

        UserResponse userResponse = userService.registerUser(newUser);

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(newUser.getId(), newUser.getNickname(), newUser.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(newUser.getId(), newUser.getNickname(), newUser.getRole().name());

        // 토큰이 생성되지 않았을 경우 처리
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_ACCESS_TOKEN"));
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "MISSING_REFRESH_TOKEN"));
        }

        // 토큰 유효성 검증
        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_ACCESS_TOKEN"));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtil.error(401, "INVALID_REFRESH_TOKEN"));
        }

        // 토큰 응답 반환
        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        log.info("{}", response);

        UserInfoResponse userInfoResponse = new UserInfoResponse(userResponse, response);

        if(pushEnabledValue) {
            fcmTokenService.saveToken(newUser.getId(), fcmToken);
            notificationService.subscribeToTopic(fcmToken, newUser.getCategory().getId(), newUser.getId());
        }

        return ResponseEntity.ok()
                .body(ApiUtil.success(userInfoResponse));
    }

    @Operation(summary = "JWT 검색", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TokenResponse.class)))
    })
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

    @Operation(summary = "Refresh JWT 발급", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TokenResponse.class)))
    })
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

        Long id = jwtUtil.getId(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(id, username, role);
        TokenResponse response = TokenResponse.builder().accessToken(newAccessToken).build();

        return ResponseEntity.ok()
                .body(ApiUtil.success(response));
    }

    @Operation(summary = "닉네임 중복 체크", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @GetMapping("/check-nickname")
    public ResponseEntity<?> validateNickname(@RequestParam("nickname") String nickname) {
        if (userService.isDuplicateNicknameRegister(nickname)) {
            return ResponseEntity.ok().body(ApiUtil.success("DUPLICATED_NICKNAME"));
        }

        return ResponseEntity.ok(ApiUtil.success("SUCCESS"));
    }

    @Operation(summary = "관리자 생성", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "SUCCESS")))
    })
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRegisterRequest request) {
        if (userService.isDuplicateNicknameRegister(request.getId())) {
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

    @Operation(summary = "관리자 로그인", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TokenResponse.class)))
    })
    @PostMapping("/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword()));

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtUtil.createAccessToken(
                    customUserDetails.getId(),
                    customUserDetails.getUsername(),
                    customUserDetails.getAuthorities().iterator().next().getAuthority()
            );

            String refreshToken = jwtUtil.createRefreshToken(
                    customUserDetails.getId(),
                    customUserDetails.getUsername(),
                    customUserDetails.getAuthorities().iterator().next().getAuthority()
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

    // USER 정보 확인용 (소셜로그인)
    @Hidden
    @GetMapping(value = "/me/user", produces = "application/json")
    public ResponseEntity<Object> getCurrentUserId(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        log.info("user info: {} {} {}", customOAuth2User.getId(), customOAuth2User.getNickname(), customOAuth2User.getRole());
        return ResponseEntity.ok().body("AuthenticationPrincipal ID: " + customOAuth2User.getId());
    }

    // ADMIN 정보 확인용 (일반로그인)
    @Hidden
    @GetMapping(value = "/me/admin", produces = "application/json")
    public ResponseEntity<Object> getCurrentAdminId(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("admin info: {} {} {}", customUserDetails.getId(), customUserDetails.getUsername(), customUserDetails.getAuthorities().iterator().next().getAuthority());
        return ResponseEntity.ok().body("AuthenticationPrincipal ID: " + customUserDetails.getId());
    }
}


