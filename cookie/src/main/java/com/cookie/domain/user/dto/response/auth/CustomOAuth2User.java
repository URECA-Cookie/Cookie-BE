package com.cookie.domain.user.dto.response.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final OAuth2UserResponse oAuth2UserResponse;

    public CustomOAuth2User(OAuth2UserResponse oAuth2UserResponse) {
        this.oAuth2UserResponse = oAuth2UserResponse;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", getId());
        attributes.put("nickname", getNickname());
        attributes.put("email", getEmail());
        attributes.put("socialId", getSocialId());
        attributes.put("socialProvider", getSocialProvider());
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return oAuth2UserResponse.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return oAuth2UserResponse.getSocialId();
    }

    public String getNickname() {
        return oAuth2UserResponse.getNickname();
    }

    public String getEmail() {
        return oAuth2UserResponse.getEmail();
    }

    public String getSocialId() {
        return oAuth2UserResponse.getSocialId();
    }

    public String getSocialProvider() {
        return oAuth2UserResponse.getSocialProvider().name();
    }

    public boolean isRegistrationRequired() {
        return oAuth2UserResponse.isRegistrationRequired();
    }

    public String getRole() {
        return oAuth2UserResponse.getRole();
    }

    public Long getId() {
        return oAuth2UserResponse.getId();
    }
}