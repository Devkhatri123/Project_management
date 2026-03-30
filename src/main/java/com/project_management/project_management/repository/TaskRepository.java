package com.project_management.project_management.repository;

import com.project_management.project_management.enums.Task_Enums.ReminderEnum;
import com.project_management.project_management.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Query("SELECT task From Task task " +
            "left join fetch task.assignee task_assignee" +
            "left join fetch task.createdBy task_creator " +
            "left join fetch task.task_attachments task_attachments " +
            "left join fetch task.task_status task_status " +
            "left join fetch task.task_tags task_tags " +
            "left join fetch task.project task_project where task.task_id = :task_id")
    Optional<Task> getTaskById(@Param("task_id") String task_id);

    @Query("SELECT task FROM Task task LEFT JOIN FETCH task.task_status LEFT JOIN FETCH task.assignee LEFT JOIN FETCH task.project WHERE (task.dueDate BETWEEN :current_time AND :thirtyMinuteFromNow) or (NOW() >= task.dueDate AND task.reminder_status = 'PENDING')")
    List<Task> findDueReminderTask(@Param("current_time") Instant current_time, @Param("thirtyMinuteFromNow") Instant thirtyMinuteFromNow);
    @Modifying
    @Query("UPDATE Task task SET task.reminder_status = 'SENT' WHERE task.task_id = :task_id")
    void changeTaskReminderStatus(@Param("task_id") String task_id);
    @Modifying
    @Query("UPDATE Task task SET task.task_status = (SELECT status FROM Status status WHERE status.status_name = 'Overdue') WHERE NOW() > task.dueDate AND task.task_status != (SELECT status FROM Status status WHERE status.status_name = 'Overdue')")
    void changeTaskStatusToOverDue();


}
