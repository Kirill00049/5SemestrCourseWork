package com.example.websockets.service;

import com.example.websockets.model.Message;
import com.example.websockets.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message create(Message message) {
        return messageRepository.save(message);
    }

    public Optional<Message> getById(Long id) {
        return messageRepository.findById(id);
    }

    public void delete(Long id) {
        messageRepository.deleteById(id);
    }
}
