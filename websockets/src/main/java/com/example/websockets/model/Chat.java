package com.example.websockets.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String secret;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn
    private Collection<Message> messages;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "users")
    private List<String> users = new ArrayList<>();

    public String getSecret() {
        return secret;
    }

    public Collection<Message> getMessages() {
        return messages;
    }
    public void addMessage (Message message) {
        messages.add(message);
    }
    public void addUser(String user) {
        users.add(user);
    }
    public String getUser(String secretUser) {
        return "User_" + users.indexOf(secretUser);
    }
}
