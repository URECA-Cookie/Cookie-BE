package com.cookie.domain.matchup.controller;


import com.cookie.domain.matchup.dto.request.ChatMessageRequest;
import com.cookie.domain.matchup.dto.response.ChatMessageResponse;
import com.cookie.domain.matchup.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
    public ChatMessageResponse sendMessage(@DestinationVariable Long matchUpId, ChatMessageRequest chatMessageRequest) {
        // TODO: JWT userId 변경
        Long senderUserId = 1L;
        return chatService.saveMessage(matchUpId, chatMessageRequest, senderUserId);
    }
}
