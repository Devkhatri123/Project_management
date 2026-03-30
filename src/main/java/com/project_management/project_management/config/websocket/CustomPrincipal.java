package com.project_management.project_management.config.websocket;

import java.security.Principal;

public class CustomPrincipal implements Principal {
    private final String principal_UserId;

    public CustomPrincipal(String user_id){
        this.principal_UserId = user_id;
    }

    @Override
    public String getName() {
        return principal_UserId;
    }
}
