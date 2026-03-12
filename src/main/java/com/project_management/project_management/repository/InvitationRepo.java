package com.project_management.project_management.repository;

import com.project_management.project_management.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepo extends JpaRepository<Invitation, String> {
    Optional<Invitation> findOneByLink(String link);
}
