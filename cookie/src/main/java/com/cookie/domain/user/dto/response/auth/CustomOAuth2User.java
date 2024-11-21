package com.cookie.domain.user.dto.response.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final OAuth2UserResponse oAuth2UserResponse;

    public CustomOAuth2User(OAuth2UserResponse oAuth2UserResponse) {
        this.oAuth2UserResponse = oAuth2UserResponse;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return oAuth2UserResponse.getRole().toString();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getNickname() {
        return oAuth2UserResponse.getNickname();
    }
}
