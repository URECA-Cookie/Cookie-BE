package com.cookie.domain.matchup.controller;


import com.cookie.domain.matchup.dto.request.ChatMessageRequest;
import com.cookie.domain.matchup.dto.response.ChatMessageResponse;
import com.cookie.domain.matchup.service.ChatService;
import com.cookie.domain.user.dto.response.auth.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

/**
 * 웹소켓 관련 기능
 */
@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatService chatService;

    @MessageMapping("/chat/{matchUpId}/messages") // app/chat/{matchUpId}/messages
    @SendTo("/topic/chat/{matchUpId}")
    public ChatMessageResponse sendMessage(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @DestinationVariable Long matchUpId, ChatMessageRequest chatMessageRequest) {
        Long senderUserId = customOAuth2User.getId();
        return chatService.saveMessage(matchUpId, chatMessageRequest, senderUserId);
    }
}
