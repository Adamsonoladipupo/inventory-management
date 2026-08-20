package com.inventory_management.notification.api;

public interface EmailService {
    void send(String to, String subject, String html);
}
