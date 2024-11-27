package com.cookie.global.jwt;

import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.domain.user.dto.response.auth.OAuth2UserResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        log.info("Auth Token: {}", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("Authorization header is missing or does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            if (!jwtUtil.validateToken(token)) {
                if (jwtUtil.isExpired(token)) {
                    log.info("Access token is expired");

                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"statusCode\": \"401\", \"message\": \"TOKEN_EXPIRED\"}");
                    return;
                }

                log.error("Invalid token detected");
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"statusCode\": \"401\", \"message\": \"UNAUTHORIZED\"}");
                return;
            }

            // 사용자 정보 파싱
            String nickname = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse();
            oAuth2UserResponse.setRole(role);
            oAuth2UserResponse.setNickname(nickname);

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2UserResponse);

            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customOAuth2User, null, customOAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            log.error("Error processing JWT", e);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"statusCode\": \"401\", \"message\": \"UNAUTHORIZED\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/authorization") || path.startsWith("/login/oauth2/code");
    }

}