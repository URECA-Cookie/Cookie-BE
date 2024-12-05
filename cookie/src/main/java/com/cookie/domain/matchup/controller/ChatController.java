package com.cookie.domain.matchup.controller;

import com.cookie.domain.matchup.dto.response.ChatMessageResponse;
import com.cookie.domain.matchup.service.ChatService;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "매치 업 채팅", description = "매치 업 채팅 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matchup-chat")
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "매치 업 채팅 정보", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ChatMessageResponse.class))))
    })
    @GetMapping("/{matchUpId}/messages")
    public ApiSuccess<?> getMessages(@PathVariable Long matchUpId) {
        List<ChatMessageResponse> messages = chatService.getMessagesByMatchUpId(matchUpId);
        return ApiUtil.success(messages);
    }
}
