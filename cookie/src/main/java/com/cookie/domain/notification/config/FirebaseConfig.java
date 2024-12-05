package com.cookie.domain.notification.config;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.path}")
    private String path;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        InputStream is = new ClassPathResource(path).getInputStream();
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(is))
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        return FirebaseMessaging.getInstance();
    }
}
