package com.cookie.domain.matchup.controller;

import com.cookie.domain.matchup.dto.response.ChatMessageResponse;
import com.cookie.domain.matchup.service.ChatService;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import com.cookie.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matchup-chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{matchUpId}/messages")
    public ApiSuccess<?> getMessages(@PathVariable Long matchUpId) {
        List<ChatMessageResponse> messages = chatService.getMessagesByMatchUpId(matchUpId);
        return ApiUtil.success(messages);
    }
}
