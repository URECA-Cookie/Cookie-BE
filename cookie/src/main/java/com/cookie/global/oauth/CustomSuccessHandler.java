package com.cookie.global.oauth;

import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import com.cookie.global.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Value("${app.client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String nickname = customUserDetails.getNickname();
        boolean isRegistrationRequired = customUserDetails.isRegistrationRequired();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        if (clientUrl == null || clientUrl.isEmpty()) {
            throw new IllegalStateException("Client URL is not configured.");
        }

        if (isRegistrationRequired) {
            log.warn("사용자 등록이 필요함");
            String redirectUrl = clientUrl + "/register"
                    + "?provider=" + customUserDetails.getSocialProvider()
                    + "&email=" + customUserDetails.getEmail()
                    + "&socialId=" + customUserDetails.getSocialId();

            response.sendRedirect(redirectUrl);
        } else {
            log.info("기존 사용자 로그인");
            String token = jwtUtil.createJwt(nickname, role, 60 * 60 * 60L);
            response.addCookie(createCookie("Authorization", token));

            response.sendRedirect(clientUrl + "/retrieve-token");
        }
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
