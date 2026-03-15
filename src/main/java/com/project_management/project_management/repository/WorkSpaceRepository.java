package com.project_management.project_management.repository;

import com.project_management.project_management.model.User;
import com.project_management.project_management.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpace, String> {
    void deleteByKey(String key);
    Optional<WorkSpace> findOneByKey(String key);

    @Query("SELECT w FROM WorkSpace w LEFT JOIN FETCH w.workspace_employees WHERE w.key= :workSpaceKey")
    Optional<WorkSpace> findWithJoinedEmployees(@Param("workSpaceKey") String workSpaceKey);

    @Query("SELECT w FROM WorkSpace w LEFT JOIN FETCH w.my_projects WHERE w.key= :workSpaceKey")
    Optional<WorkSpace> findWithCreatedProjects(@Param("workSpaceKey") String workSpaceKey);

    List<WorkSpace> findByOwner(User owner);
}
