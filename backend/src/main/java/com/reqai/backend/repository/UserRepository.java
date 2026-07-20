package com.reqai.backend.repository;

import com.reqai.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // güvenlik görevlisi veritabanından kullanıcıyı ismiyle rahat bulsun diye
    @Cacheable(value = "users", key = "#username")
    Optional<User> findByUsername(String username);

    // kalıcı API token ile kullanıcıyı bul
    @Cacheable(value = "tokens", key = "#token")
    Optional<User> findByToken(String token);
}
