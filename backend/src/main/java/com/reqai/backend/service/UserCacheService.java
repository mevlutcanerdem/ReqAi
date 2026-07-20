package com.reqai.backend.service;

import com.reqai.backend.entity.User;
import com.reqai.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Kullanıcı bilgilerini Redis önbelleğiyle yöneten servis.
 * 
 * Akış:
 * 1. Register → DB'ye kaydeder + Redis'e yazar (@CachePut)
 * 2. Login   → Önce Redis'e bakar, yoksa DB'den çeker (@Cacheable)
 * 3. Her API isteği → Token'ı Redis'ten doğrular (@Cacheable)
 * 
 * Bu sayede binlerce kullanıcı aynı anda giriş yapsa bile
 * Postgres'e değil Redis'e (RAM) gider → Jet hızında çalışır.
 */
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserRepository userRepository;

    /**
     * Register sonrası kullanıcıyı Redis'e yazar.
     * @CachePut: Her zaman metodu çalıştırır ve sonucu cache'e yazar.
     * Key = username, böylece login'de username ile hızlıca bulunur.
     */
    @CachePut(value = "users", key = "#user.username")
    public User cacheAfterRegister(User user) {
        return user;
    }

    /**
     * Login sırasında kullanıcıyı önce Redis'ten arar.
     * Cache'de varsa → DB'ye hiç gitmez (süper hızlı).
     * Cache'de yoksa → DB'den çeker ve Redis'e yazar (bir sonraki sefer hızlı).
     */
    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    /**
     * Token ile kullanıcı doğrulama (her API isteğinde çalışır).
     * Cache'de varsa → DB'ye gitmez.
     */
    @Cacheable(value = "tokens", key = "#token")
    public User findByToken(String token) {
        return userRepository.findByToken(token)
                .orElse(null);
    }

    /**
     * Kullanıcı bilgisi güncellendiğinde eski cache'i temizler.
     */
    @CacheEvict(value = "users", key = "#username")
    public void evictUserCache(String username) {
        // Cache temizlenir, bir sonraki istekte DB'den taze veri çekilir
    }
}
