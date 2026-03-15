package com.project_management.project_management.repository;

import com.project_management.project_management.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, String> {
    @Query("SELECT status FROM Status status WHERE status.status_name = :status_name")
    Optional<Status> getStatusByName(@Param("status_name") String status_name);
}
