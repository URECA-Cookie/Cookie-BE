package com.cookie.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {


    @Bean(name = "mainAdminRecommendCacheManager")
    @Primary
    public CacheManager mainAdminCachManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("mainAdminRecommendCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES));
        return cacheManager;
    }


    @Bean(name = "mainMatchUpCacheManager")
    public CacheManager mainMatchUpCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("mainMatchUpCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES));
        return cacheManager;
    }

    //@Primary
    @Bean(name = "categoryMoviesCache")
    public CacheManager categoryMoviesCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("categoryMoviesCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES));
        return cacheManager;
    }

    // Reward ...
    @Bean(name = "pointLivedCache")
    public Caffeine<Object, Object> pointCaffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .recordStats()
                .expireAfterWrite(30, TimeUnit.DAYS);
    }

    @Bean(name = "pointLivedCacheManager")
    public CacheManager pointCacheManager(@Qualifier("pointLivedCache") Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("pointLivedCache");
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}

