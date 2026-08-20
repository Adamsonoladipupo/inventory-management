package com.inventory_management.notification.exception;

public interface NotificationService {
    void sendOwnerWelcomeEmail(String email, String name);

    void sendUserInvitationEmail(String email, String name, String businessName, String invitationToken);

    void sendRegistrationCompletedEmail(String email, String name);
}
