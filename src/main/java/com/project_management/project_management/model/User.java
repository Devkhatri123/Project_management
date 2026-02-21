package com.project_management.project_management.model;

import jakarta.persistence.*;
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
    private String name;
    private String email;
    private String password;
    private String profile_pic;
    private String title;
    private boolean is_enabled;
    private String role;

    @OneToOne(cascade = {CascadeType.ALL})
    private Verification verification;
    @OneToMany
    private List<WorkSpace> myWorkSpaces;
    @OneToOne(cascade = CascadeType.ALL)
    private ForgetPassword forgetPassword;
}
