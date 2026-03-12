package com.project_management.project_management.service;

import com.project_management.project_management.exception.workspace.WorkSpaceInvitationLinkNotFound;
import com.project_management.project_management.model.Invitation;
import com.project_management.project_management.repository.InvitationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {
    private final InvitationRepo invitationRepo;

    @Autowired
    public InvitationService(final InvitationRepo invitationRepo){
        this.invitationRepo = invitationRepo;
    }
    public Invitation getInvitationRequestByLink(String invitationLink) throws WorkSpaceInvitationLinkNotFound {
        return invitationRepo.findOneByLink(invitationLink)
                .orElseThrow(() -> new WorkSpaceInvitationLinkNotFound("Invitation link has been expired"));
    }
    public void deleteInvitation(String invitationId){
        invitationRepo.deleteById(invitationId);
    }
}
