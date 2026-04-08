package com.project_management.project_management.repository;

import com.project_management.project_management.model.Project;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("SELECT project FROM Project project left join fetch project.project_tasks WHERE project.project_id = :project_id")
    Optional<Project> findProjectWithTask(@Param("project_id") String project_id);
    @Query("SELECT project FROM Project project WHERE project.project_id = :id")
    Optional<Project> findOnlyProjectById(@Param("id") String id);
    @Query("select project from Project project left join fetch project.project_assignees " +
            "left join fetch project.project_tasks where project.project_id = :project_id")
    Optional<Project> findProjectWithProjectAssigneesAndTask(@Param("project_id") String project_id);

    @Query("select project from Project project left join fetch project.project_assignees where project.project_id = :project_id")
    Optional<Project> getProjectAssignees(@Param("project_id") String project_id);
    @Query("select project from Project project where project.workSpace.workSpace_id = :workspace_id")
    List<Project> getWorkspaceProjects(@Param("workspace_id") String workspace_id, Pageable pageable);

}
