package com.example.websockets.controller;

import com.example.websockets.model.Chat;
import com.example.websockets.model.Message;
import com.example.websockets.service.ChatService;
import com.example.websockets.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;

@Controller
public class AppController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final ChatService chatService;
    private final MessageService messageService;

    @Autowired
    public AppController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @Async("asyncTaskExecutor")
    @MessageMapping("/newChat")
    public void newChat(SimpMessageHeaderAccessor sha) {
        Chat chat = chatService.createChat(new Chat());
        chat = chatService.newUser(sha.getUser().getName(), chat.getSecret());
        chat = chatService.newMessage(
                messageService.create(new Message("Создан новый чат, приятного общения!", "System")),
                chat.getSecret()
        );
        simpMessagingTemplate.convertAndSendToUser(sha.getUser().getName(), "/queue/chat", chat.getSecret());
        for (var message : chat.getMessages()) {
            simpMessagingTemplate.convertAndSend("/queue/"+chat.getSecret(), message.toString());
        }
    }

    @Async("asyncTaskExecutor")
    @MessageMapping("/openChat")
    public void openChat(SimpMessageHeaderAccessor sha, @RequestParam String secretOpen) {
        try {
            Chat chat = chatService.getChat(secretOpen).orElseThrow();
            String findName = chat.getUser(sha.getUser().getName());
            if (findName.equals("User_-1")) {
                chat = chatService.newUser(sha.getUser().getName(), secretOpen);
                Message message = new Message(chat.getUser(sha.getUser().getName()) + " зашел в чат!", "System");
                chat = chatService.newMessage(
                        messageService.create(message),
                        chat.getSecret()
                );
                Thread.sleep(2000);
                simpMessagingTemplate.convertAndSend("/queue/"+chat.getSecret(), message.toString());
            }
        } catch (Exception e) {
            simpMessagingTemplate.convertAndSendToUser(sha.getUser().getName(), "/queue/" + secretOpen, "Такого чата не существует");
        }
    }

    @Async("asyncTaskExecutor")
    @MessageMapping("/sendMessage")
    public void sendMessage(SimpMessageHeaderAccessor sha, @Header String secret, @Payload String msg) {
        try {
            Chat chat = chatService.getChat(secret).orElseThrow();
            Message message = messageService.create(new Message(msg, chat.getUser(sha.getUser().getName())));
            chat = chatService.newMessage(message, chat.getSecret());
            simpMessagingTemplate.convertAndSend("/queue/" + secret, message.toString());
        } catch (Exception e) {
            simpMessagingTemplate.convertAndSendToUser(sha.getUser().getName(), "/queue/" + secret, "Такого чата не существует");
        }
    }

    @Async("asyncTaskExecutor")
    @MessageMapping("/loadMessages")
    public void loadMessages(SimpMessageHeaderAccessor sha, @Header String secret){
        try {
            Chat chat = chatService.getChat(secret).orElseThrow();

            for (var msg : chat.getMessages().stream().sorted(Comparator.comparing(Message::getDateTime)).toList()) {
                simpMessagingTemplate.convertAndSendToUser(sha.getUser().getName(), "/queue/" + secret, msg.toString());
                Thread.sleep(10);
            }

        } catch (Exception e) {
            simpMessagingTemplate.convertAndSendToUser(sha.getUser().getName(), "/queue/" + secret, "Такого чата не существует");
        }
    }
}
