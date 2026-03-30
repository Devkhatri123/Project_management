package com.project_management.project_management.listener;

import com.project_management.project_management.event.TaskCreatedEvent;
import com.project_management.project_management.service.email.task.TaskEmailService;
import com.project_management.project_management.util.TaskUtil;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class TaskCreatedListener {
    private final TaskEmailService taskEmailService;
    @Autowired
    public TaskCreatedListener(final TaskEmailService taskEmailService){
        this.taskEmailService = taskEmailService;
    }

    @Async
    @TransactionalEventListener
    public void sendRegistrationSuccessfulEmail(TaskCreatedEvent taskCreatedEvent) throws MessagingException {
        String formattedTaskDeadLine = TaskUtil.formatTaskTime(taskCreatedEvent.getCreatedTask().getDueDate());
        taskEmailService.sendTaskAssignedEmail(taskCreatedEvent.getCreatedTask(), formattedTaskDeadLine);
    }
}
