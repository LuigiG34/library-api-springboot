package com.example.libraryapi.notification;

public interface NotificationSender {

    void sendNotification(String message, String recipientEmail);

}
