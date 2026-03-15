package com.project_management.project_management.listener;

import com.project_management.project_management.event.JoinInvitationEvent;
import com.project_management.project_management.service.email.workspace.WorkSpaceEmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class JoinInvitationListener {
    private final WorkSpaceEmailService workSpaceEmailService;

    @Autowired
    public JoinInvitationListener(final WorkSpaceEmailService workSpaceEmailService){
        this.workSpaceEmailService = workSpaceEmailService;
    }
    @Async
    @TransactionalEventListener
    public void sendJoinInvitationEmail(JoinInvitationEvent joinInvitationEvent) throws MessagingException {
        workSpaceEmailService.sendWorkSpaceJoinInvitationEmail(joinInvitationEvent.getInvitation());
    }
}
