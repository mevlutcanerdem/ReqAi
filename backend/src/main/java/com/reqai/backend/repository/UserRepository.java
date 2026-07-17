package com.reqai.backend.repository;

import com.reqai.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // güvenlik görevlisi veritabanından kullanıcıyı ismiyle rahat bulsun diye
    Optional<User> findByUsername(String username);
}
