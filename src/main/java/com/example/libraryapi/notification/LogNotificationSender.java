package com.example.libraryapi.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary 
@Component 
public class LogNotificationSender implements NotificationSender {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LogNotificationSender.class);

    @Override 
    public void sendNotification(String message, String recipientEmail) {
        logger.info("Logging notification for " + recipientEmail + ": " + message);
    }
}
