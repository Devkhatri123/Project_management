package com.project_management.project_management.repository;

import com.project_management.project_management.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    RefreshToken findByToken(String token);

    void deleteByToken(String token);
}
