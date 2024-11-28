package com.cookie.domain.matchup.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "match_up_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    private String id;
    private Long matchUpId;
    private Long senderUserId;
    private String senderNickname;
    private String senderProfileImage;
    private String content;
    private LocalDateTime sentAt;

    @Builder
    public ChatMessage(Long matchUpId, Long senderUserId, String senderNickname, String senderProfileImage, String content, LocalDateTime sentAt) {
        this.matchUpId = matchUpId;
        this.senderUserId = senderUserId;
        this.senderNickname = senderNickname;
        this.senderProfileImage = senderProfileImage;
        this.content = content;
        this.sentAt = sentAt;
    }
}