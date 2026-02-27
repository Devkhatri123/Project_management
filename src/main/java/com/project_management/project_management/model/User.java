package com.project_management.project_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotBlank(message = "name cannot be empty")
    private String name;
    @NotBlank(message = "email cannot be empty")
    private String email;
    @NotBlank(message = "password cannot be empty")
    private String password;
    private String profile_pic;
    private String title;
    private boolean is_enabled;
    @NotBlank(message = "role cannot be empty")
    private String role;

    @OneToOne(mappedBy = "user",cascade = {CascadeType.ALL})
    private Verification verification;
    @OneToMany(cascade = CascadeType.ALL)
    private List<WorkSpace> myWorkSpaces;
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private ForgetPassword forgetPassword;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;
}
