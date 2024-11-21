package com.cookie.global.jwt;

import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.domain.user.dto.response.auth.OAuth2UserResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    authorization = cookie.getValue();
                    break;
                }
            }
        }

        if (authorization == null) {
            log.info("token null");
            filterChain.doFilter(request, response);

            return;
        }

        String token = authorization;

        if (jwtUtil.isExpired(token)) {
            log.info("token expired");
            filterChain.doFilter(request, response);

            return;
        }

        String nickname = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse();
        oAuth2UserResponse.setRole(role);
        oAuth2UserResponse.setNickname(nickname);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2UserResponse);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
