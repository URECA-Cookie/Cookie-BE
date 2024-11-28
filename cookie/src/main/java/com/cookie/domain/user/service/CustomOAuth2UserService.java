package com.cookie.domain.user.service;

import com.cookie.domain.user.dto.response.auth.*;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 요청: {}", registrationId);

        // Provider에 따른 응답 데이터 전처리
        OAuth2Response oAuth2Response = null;
        SocialProvider socialProvider = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            socialProvider = SocialProvider.NAVER;
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            socialProvider = SocialProvider.GOOGLE;
        } else {
            log.error("지원되지 않는 소셜 로그인 Provider: {}", registrationId);
            return null;
        }

        log.info("소셜 로그인 사용자 정보: Provider={}, ProviderId={}, Email={}",
                socialProvider, oAuth2Response.getProviderId(), oAuth2Response.getEmail());

        // 로그인 완료 후 로직
        User existData = userRepository.findBySocialProviderAndSocialId(socialProvider, oAuth2Response.getProviderId());

        OAuth2UserResponse user = new OAuth2UserResponse();
        if (existData == null) {
            log.warn("소셜 사용자 등록 필요: Provider={}, ProviderId={}", socialProvider, oAuth2Response.getProviderId());

            user.setSocialProvider(socialProvider);
            user.setEmail(oAuth2Response.getEmail());
            user.setSocialId(oAuth2Response.getProviderId());
            user.setRegistrationRequired(true);

        } else {
            log.info("기존 사용자 로그인 성공: Id={}, Nickname={}, Role={}", existData.getId(), existData.getNickname(), existData.getRole());
            user.setId(existData.getId());
            user.setSocialProvider(existData.getSocialProvider());
            user.setEmail(existData.getEmail());
            user.setSocialId(existData.getSocialId());
            user.setRole(existData.getRole().name());
            user.setNickname(existData.getNickname());
            user.setRegistrationRequired(false);
        }

        return new CustomOAuth2User(user);
    }
}
