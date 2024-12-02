package com.cookie.domain.matchup.controller;


import com.cookie.domain.matchup.dto.request.ChatMessageRequest;
import com.cookie.domain.matchup.dto.response.ChatMessageResponse;
import com.cookie.domain.matchup.service.ChatService;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Objects;

/**
 * 웹소켓 관련 기능
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    private final ChatService chatService;
//    private final JWTUtil jwtUtil;

    @MessageMapping("/chat/{matchUpId}/messages") // app/chat/{matchUpId}/messages
    @SendTo("/topic/chat/{matchUpId}")
    public ChatMessageResponse sendMessage(SimpMessageHeaderAccessor accessor, @DestinationVariable Long matchUpId, ChatMessageRequest chatMessageRequest) {
        String userId = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("User");
        Long senderUserId = Long.parseLong(userId);
        log.info("senderID: {}",userId);
        return chatService.saveMessage(matchUpId, chatMessageRequest, senderUserId);
    }
}
