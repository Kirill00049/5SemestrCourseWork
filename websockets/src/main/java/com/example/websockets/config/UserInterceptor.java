package com.example.websockets.config;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.example.websockets.model.User;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class UserInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                Object name = ((Map<?, ?>) raw).get("username");
                String salt = StringGenerator.generateLongString(
                        ((ArrayList<String>) name).get(0),
                        accessor.getPasscode(),
                        255
                );
                accessor.setUser(new User(
                        salt
                ));
            }
        }
        return message;
    }
}
