package com.project_management.project_management.Dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private String name;
    private String email;
    private String title;
    private String profile_pic;
    private String role;
    private String is_enabled;
    private List<String> authorities;
}
