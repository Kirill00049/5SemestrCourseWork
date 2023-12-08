package com.example.websockets.service;

import com.example.websockets.model.Chat;
import com.example.websockets.model.Message;
import com.example.websockets.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private ConcurrentHashMap<String, Boolean> usersOnlineBusy = new ConcurrentHashMap<>();

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }


    public Chat createChat(Chat chat){
        return chatRepository.save(chat);
    }

    public Optional<Chat> getChat(String secret) {
        return chatRepository.findBySecret(secret);
    }

    public Chat newMessage(Message message, String secret) {
        System.out.println(message);
        Optional<Chat> chat = chatRepository.findBySecret(secret);
        if (chat.isEmpty()) {
            return null;
        }
        chat.get().addMessage(message);
        return chatRepository.save(chat.get());
    }

    public Chat newUser(String user, String secret) {
        Optional<Chat> chat = chatRepository.findBySecret(secret);
        if (chat.isEmpty()) {
            return null;
        }
        chat.get().addUser(user);
        return chatRepository.save(chat.get());
    }

    public void removeChat(String secret) {
        chatRepository.deleteBySecret(secret);
    }
}
