package com.cookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CookieApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookieApplication.class, args);
    }

}
