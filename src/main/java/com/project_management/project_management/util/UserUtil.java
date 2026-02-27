package com.project_management.project_management.util;

import com.project_management.project_management.model.User;
import com.project_management.project_management.model.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
    public static User getCurrentUser(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}
