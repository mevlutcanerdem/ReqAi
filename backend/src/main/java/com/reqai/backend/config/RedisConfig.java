package com.reqai.backend.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
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
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        // Value serializer: varsayılan JDK serialization kullanılır (User implements Serializable)
        // Jackson serializer Hibernate proxy nesneleriyle çakışma yapabilir
    }

    /**
     * Redis bağlantısı koptuğunda uygulamanın çökmesini önler.
     * Cache hatası olursa sessizce loglar ve doğrudan veritabanından devam eder.
     */
    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new org.springframework.cache.interceptor.SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                System.err.println("[REDIS-WARN] Cache GET hatası (sessizce atlanıyor): " + exception.getMessage());
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