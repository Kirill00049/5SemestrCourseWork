package com.example.websockets.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private LocalDateTime dateTime = LocalDateTime.now();
    private String sender;

    public Message(String text, String sender) {
        this.text = text;
        this.sender = sender;
        this.dateTime = LocalDateTime.now();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Message() {
    }

    @Override
    public String toString() {
        return sender + ": " + text + "(в " + dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + ")";
    }
}
