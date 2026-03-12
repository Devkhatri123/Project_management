package com.project_management.project_management.model;

import com.project_management.project_management.enums.User_Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

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
    @Column(unique = true)
    private String email;
    @NotBlank(message = "password cannot be empty")
    private String password;
    private String profile_pic;
    private String title;
    private boolean is_enabled;
    @NotNull(message = "role cannot be empty")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_id")
    private Verification verification;
    // created workspace
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<WorkSpace> myWorkSpaces;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    private ForgetPassword forgetPassword;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RefreshToken refreshToken;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", referencedColumnName = "subscription_id")
    private Subscription subscription;
    // Joined workspace
    @ManyToMany
    private List<WorkSpace> joined_workspace;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
