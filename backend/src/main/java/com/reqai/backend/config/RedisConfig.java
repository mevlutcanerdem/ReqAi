package com.reqai.backend.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    /**
     * Redis bağlantısı koptuğunda uygulamanın çökmesini önler.
     * Cache hatası olursa sessizce loglar ve doğrudan veritabanından devam eder.
     * Bu sayede Redis opsiyonel hale gelir - varsa hızlandırır, yoksa uygulama yine çalışır.
     */
    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new org.springframework.cache.interceptor.SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                System.err.println("[REDIS-WARN] Cache GET hatası (sessizce atlanıyor): " + exception.getMessage());
                // Hata fırlatma, veritabanından devam et
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                System.err.println("[REDIS-WARN] Cache PUT hatası (sessizce atlanıyor): " + exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                System.err.println("[REDIS-WARN] Cache EVICT hatası (sessizce atlanıyor): " + exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                System.err.println("[REDIS-WARN] Cache CLEAR hatası (sessizce atlanıyor): " + exception.getMessage());
            }
        };
    }
}