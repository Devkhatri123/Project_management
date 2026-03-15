package com.project_management.project_management.event;

import com.project_management.project_management.model.Invitation;

public class JoinInvitationEvent {
    private final Invitation invitation;
    public JoinInvitationEvent(Invitation invitation){
        this.invitation = invitation;
    }
    public Invitation getInvitation(){
        return invitation;
    }
}
