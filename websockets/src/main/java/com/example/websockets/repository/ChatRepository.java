package com.example.websockets.repository;

import com.example.websockets.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findBySecret(String uuid);
    void deleteBySecret(String secret);
}
