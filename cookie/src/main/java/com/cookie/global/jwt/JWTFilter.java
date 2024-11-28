package com.cookie.global.jwt;

import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.domain.user.dto.response.auth.CustomUserDetails;
import com.cookie.domain.user.dto.response.auth.OAuth2UserResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
            Long id = jwtUtil.getId(token);
            String nickname = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            if ("USER".equals(role)) {
                OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse();
                oAuth2UserResponse.setId(id);
                oAuth2UserResponse.setRole(role);
                oAuth2UserResponse.setNickname(nickname);

                CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2UserResponse);

                Authentication authToken = new UsernamePasswordAuthenticationToken(
                        customOAuth2User, null, customOAuth2User.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } else if ("ADMIN".equals(role)) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                Authentication authToken = new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(id, nickname, null, authorities), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

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
        boolean shouldNotFilter = path.startsWith("/oauth2/authorization") || path.startsWith("/login/oauth2/code");
        log.info("Path: {} - Should not filter: {}", path, shouldNotFilter);
        return shouldNotFilter;
    }


}