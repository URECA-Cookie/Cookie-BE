package com.cookie.domain.user.service;

import com.cookie.domain.user.dto.response.auth.*;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.entity.enums.SocialProvider;
import com.cookie.domain.user.exception.RegistrationRequiredException;
import com.cookie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

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
            return null;
        }

        // 로그인 완료 후 로직
        User existData = userRepository.findBySocialProviderAndSocialId(socialProvider, oAuth2Response.getProviderId());

        if (existData == null) {
            // 사용자 정보가 없으면 클라이언트로 리다이렉트
            throw new RegistrationRequiredException(
                    User.builder()
                        .socialProvider(socialProvider)
                        .socialId(oAuth2Response.getProviderId())
                        .email(oAuth2Response.getEmail())
                        .build()
            );
        } else {
            // 기존 유저 정보 반환
            OAuth2UserResponse user = new OAuth2UserResponse();
            user.setRole(existData.getRole().name());
            user.setNickname(existData.getNickname());

            return new CustomOAuth2User(user);
        }
    }
}
