package com.cookie.domain.matchup.repository;

import com.cookie.domain.matchup.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByMatchUpIdOrderBySentAtAsc(Long matchUpId);
}
