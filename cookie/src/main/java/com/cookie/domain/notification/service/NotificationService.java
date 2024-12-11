package com.cookie.domain.notification.service;


import com.cookie.domain.category.repository.CategoryRepository;
import com.cookie.domain.category.entity.Category;
import com.cookie.domain.user.repository.UserRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FirebaseMessaging firebaseMessaging;

    public void subscribeToTopic(String token, Long categoryId, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("not found categoryId: " + categoryId));

        String topic = category.getSubCategoryEn();

        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(token), topic);
            log.info("Subscribed to topic: {}", topic);
        } catch (FirebaseMessagingException e) {
            log.error("Failed subscribe to topic: {}", e.getMessage());

        }

    }

    public void unsubscribeFromTopic(String token, Long categoryId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("not found categoryId: " + categoryId));

        String topic = category.getSubCategoryEn();

        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(token), topic);
            log.info("Unsubscribed from topic: {}", topic);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe from topic: {}", e.getMessage());
        }
    }


    public void sendPushNotificationToTopic(String topic, String title, String body) {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setTopic(topic)
                .build();

        try {
            String response = firebaseMessaging.send(message);
            log.info("Send push notification: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending push notification: {}", e.getMessage());
        }
    }


    public void sendPushNotificationToUsers(List<String> tokens, String title, String body) {
        for (String token : tokens) {
            // 작성자의 토큰은 제외
//            if (excludedTokens.contains(token)) {
//                continue;
//            }

            Message message = Message.builder()
                    .setWebpushConfig(WebpushConfig.builder()
                            .putHeader("Urgency", "high")
                            .build())
                    .setToken(token)
                    .putData("title", title)
                    .putData("body", body)
                    .setToken(token)
                    .build();

            try {
                String response = firebaseMessaging.send(message);
                log.info("Push notification sent successfully to token: {}", token);
            } catch (FirebaseMessagingException e) {
                log.error("Error sending push notification to token {}: {}", token, e.getMessage());
            }
        }
    }

}
