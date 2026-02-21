package com.project_management.project_management.repository;

import com.project_management.project_management.model.ForgetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgetPasswordRepo extends JpaRepository<ForgetPassword, String> {
    Optional<ForgetPassword> findOneByToken(String token);
}
