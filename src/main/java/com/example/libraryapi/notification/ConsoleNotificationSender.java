package com.example.libraryapi.notification;

import org.springframework.stereotype.Component;

@Component 
public class ConsoleNotificationSender implements NotificationSender {
    
    @Override 
    public void sendNotification(String message, String recipientEmail) {
        System.out.println("Sending notification to " + recipientEmail + ": " + message);
    }
}
