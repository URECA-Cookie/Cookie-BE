package com.cookie.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean(name = "mainPageCache")
    public CacheManager mainCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("mainPageCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES));
        return cacheManager;
    }
    @Primary
    @Bean(name = "categoryMoviesCache")
    public CacheManager categoryMoviesCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("categoryMoviesCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES));
        return cacheManager;
    }
}

