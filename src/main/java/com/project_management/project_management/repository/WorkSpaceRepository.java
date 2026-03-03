package com.project_management.project_management.repository;

import com.project_management.project_management.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpace, String> {
    void deleteByKey(String key);

    Optional<WorkSpace> findOneByKey(String key);
}
