package com.project_management.project_management.event;

import com.project_management.project_management.model.User;

public class UserCreatedEvent {
    private final User createdUser;
    public UserCreatedEvent(User user){
        this.createdUser = user;
    }
    public User getCreatedUser(){
        return createdUser;
    }
}
