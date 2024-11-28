package com.cookie.domain.matchup.service;

import com.cookie.domain.matchup.dto.request.ChatMessageRequest;
import com.cookie.domain.matchup.dto.response.ChatMessageResponse;
import com.cookie.domain.matchup.entity.ChatMessage;
import com.cookie.domain.matchup.repository.ChatMessageRepository;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    @Transactional
    public ChatMessageResponse saveMessage(Long matchUpId, ChatMessageRequest chatMessageRequest, Long senderUserId) {
        User user = userRepository.findById(senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("not found user: " + senderUserId));
        ChatMessage chatMessage = ChatMessage.builder()
                .matchUpId(matchUpId)
                .senderUserId(user.getId())
                .senderNickname(user.getNickname())
                .senderProfileImage(user.getProfileImage())
                .content(chatMessageRequest.getContent())
                .sentAt(LocalDateTime.now())
                .build();

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.fromEntity(savedChatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesByMatchUpId(Long matchUpId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByMatchUpIdOrderBySentAtAsc(matchUpId);
        return chatMessages.stream()
                .map(chatMessage -> new ChatMessageResponse(
                        chatMessage.getMatchUpId(),
                        chatMessage.getSenderUserId(),
                        chatMessage.getSenderNickname(),
                        chatMessage.getSenderProfileImage(),
                        chatMessage.getContent(),
                        chatMessage.getSentAt()
                ))
                .toList();
    }
}
