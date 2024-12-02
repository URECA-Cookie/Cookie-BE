package com.cookie.domain.matchup.interceptor;

import com.cookie.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (headerAccessor == null) {
            return message;
        }

        if (headerAccessor.getCommand() == StompCommand.CONNECT) {
            List<String> authHeader = headerAccessor.getNativeHeader("Authorization");
            if (authHeader == null || ((List<?>) authHeader).isEmpty()) {
                log.warn("Authorization header is missing");
                throw new IllegalArgumentException("Authorization header is required");
            }

            String token = authHeader.get(0).replace("Bearer ", "");
            log.info("token: {}", token);
            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Invalid token");
                    throw new IllegalArgumentException("Invalid token");
                }

                Long userId = jwtUtil.getId(token);
                headerAccessor.addNativeHeader("User", String.valueOf(userId));
                log.info("UserId from token: {}", userId);

                headerAccessor.getSessionAttributes().put("User", String.valueOf(userId));

            } catch (Exception e) {
                log.error("Token validation error: {}", e.getMessage());
                throw new IllegalArgumentException("Invalid or expired token");
            }
        }

        return message;
    }
}

