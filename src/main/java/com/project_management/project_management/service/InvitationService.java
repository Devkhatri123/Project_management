package com.project_management.project_management.service;

import com.project_management.project_management.enums.WorkSpace_Enums.WorkSpaceJoin_Status;
import com.project_management.project_management.exception.workspace.WorkSpaceInvitationLinkNotFound;
import com.project_management.project_management.model.Invitation;
import com.project_management.project_management.model.WorkSpace;
import com.project_management.project_management.repository.InvitationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class InvitationService {
    private final InvitationRepo invitationRepo;

    @Autowired
    public InvitationService(final InvitationRepo invitationRepo){
        this.invitationRepo = invitationRepo;
    }
    public Invitation createInvitation(String userToBeInvitedEmail, WorkSpace workSpace){
        return Invitation.builder()
                .invitedToWorkspace(workSpace)
                .link(UUID.randomUUID().toString().substring(0,10))
                .status(WorkSpaceJoin_Status.PENDING)
                .email(userToBeInvitedEmail)
                .expiresOn(LocalDateTime.now(ZoneOffset.UTC).plusDays(1))
                .build();
    }
    public Invitation getInvitationRequestByLink(String invitationLink) throws WorkSpaceInvitationLinkNotFound {
        return invitationRepo.findOneByLink(invitationLink)
                .orElseThrow(() -> new WorkSpaceInvitationLinkNotFound("Invitation link has been expired"));
    }
    public void deleteInvitation(String invitationId){
        invitationRepo.deleteById(invitationId);
    }
}
