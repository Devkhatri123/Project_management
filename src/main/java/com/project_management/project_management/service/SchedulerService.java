package com.project_management.project_management.service;

import com.project_management.project_management.enums.Task_Enums.ReminderEnum;
import com.project_management.project_management.model.Task;
import com.project_management.project_management.service.email.task.TaskEmailService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SchedulerService {
    private final TaskService taskService;
    private final TaskEmailService taskEmailService;

    public SchedulerService(final TaskService taskService, final TaskEmailService taskEmailService){
        this.taskService = taskService;
        this.taskEmailService = taskEmailService;
    }

    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.MINUTES)
    // @Transactional
    public void sendTaskDeadLineReminderEmail(){
      List<Task> reminderDueTasks = taskService.findDueReminderTasks();
      reminderDueTasks.forEach(task -> {
          long minutesRemaining = Duration.between(LocalDateTime.ofInstant(task.getDueDate(), ZoneOffset.UTC),
                  LocalDateTime.now(ZoneOffset.UTC)).toMinutes();
           if (minutesRemaining < 0 || task.getTask_status().getStatus_name().equals("Overdue")){
               try {
                   taskEmailService.sendTaskDeadLineMissed(task);
                   taskService.changeTaskReminderStatus(task.getTask_id());
                   log.info("deadline missed reminder sent of task: {}", task.getTitle());
               } catch (MessagingException e) {
                   log.error("Exception in sending reminder email: {}", e.getMessage());
                   throw new RuntimeException(e);
               }
           } else if (task.getTask_status().getStatus_name().equals("To Do")) {
               // Send upcoming deadline email
               try {
                   taskEmailService.sendTaskDeadLineUpComingReminder(task, (int) minutesRemaining);
                   taskService.changeTaskReminderStatus(task.getTask_id());
                   log.info("deadline upcoming reminder sent of task: {}", task.getTitle());
               } catch (MessagingException e) {
                   log.error("Exception in sending reminder email: {}", e.getMessage());
               }
           }
      });
    }
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void changeTaskStatusFromInProgressToOverdue(){
     taskService.changeTaskStatusToOverDue();
     log.info("Task whose deadline has been passed, that task status has been changed to overdue");
    }
}
