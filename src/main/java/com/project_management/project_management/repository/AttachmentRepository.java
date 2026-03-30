package com.project_management.project_management.repository;

import com.project_management.project_management.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    @Query("SELECT count(attachment.attachment_url) FROM Attachment attachment WHERE attachment.attachment_Owner.task_id = :task_id")
    long countByTaskId(@Param("task_id") String task_id);
}
