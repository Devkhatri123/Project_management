package com.project_management.project_management.listener;

import com.project_management.project_management.event.UserCreatedEvent;
import com.project_management.project_management.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailNotificationListener {
    private final EmailService emailService;

    @Autowired
    public EmailNotificationListener(final EmailService emailService){
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener
    public void sendRegistrationSuccessfulEmail(UserCreatedEvent userCreatedEvent) throws MessagingException {
     emailService.registrationSuccessfulEmail(userCreatedEvent.getCreatedUser());
    }
}
