package com.cookie.domain.user.service;

import com.cookie.domain.user.dto.response.auth.CustomUserDetails;
import com.cookie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByNickname(username)
                .map(user -> new CustomUserDetails(
                        user.getId(),
                        user.getNickname(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority(user.getRole().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("NOT_FOUND_USER"));
    }

}
