package com.project_management.project_management.repository;

import com.project_management.project_management.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("SELECT project FROM Project project left join fetch project.project_tasks WHERE project.project_id = :project_id")
    Optional<Project> findProjectWithTask(@Param("project_id") String project_id);
    @Query("SELECT project FROM Project project WHERE project.project_id = :id")
    Optional<Project> findOnlyProjectById(@Param("id") String id);
}
