package com.cookie.domain.matchup.dto.response;

import com.cookie.domain.matchup.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long matchUpId;
    private Long senderUserId;
    private String senderNickname;
    private String senderProfileImage;
    private String content;
    private LocalDateTime sentAt;

    public static ChatMessageResponse fromEntity(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getMatchUpId(),
                chatMessage.getSenderUserId(),
                chatMessage.getSenderNickname(),
                chatMessage.getSenderProfileImage(),
                chatMessage.getContent(),
                chatMessage.getSentAt()
        );
    }
}
