package com.project_management.project_management.service;

import com.project_management.project_management.Dtos.task.ChangeStatusDTO;
import com.project_management.project_management.Dtos.task.CreateTaskDTO;
import com.project_management.project_management.Dtos.task.TaskUserDTO;
import com.project_management.project_management.Dtos.task.TaskDTO;
import com.project_management.project_management.event.TaskCreatedEvent;
import com.project_management.project_management.exception.LimitReached;
import com.project_management.project_management.exception.project.ProjectNotFound;
import com.project_management.project_management.exception.task.InvalidStatusSelected;
import com.project_management.project_management.exception.task.StatusNotFound;
import com.project_management.project_management.exception.task.TaskCreationLimitHasBeenReached;
import com.project_management.project_management.exception.task.TaskNotFound;
import com.project_management.project_management.exception.user.UserNotFound;
import com.project_management.project_management.model.*;
import com.project_management.project_management.repository.TaskRepository;
import com.project_management.project_management.util.UserUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final ProjectService projectService;
    private final UserService userService;
    private final StatusService statusService;
    private final TagService tagService;
    private final AttachmentService attachmentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public TaskService(final TaskRepository taskRepository, final ModelMapper modelMapper,
                       final ProjectService projectService, final UserService userService,
                       final StatusService statusService, final TagService tagService,
                       final AttachmentService attachmentService, final ApplicationEventPublisher applicationEventPublisher){
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.projectService = projectService;
        this.userService = userService;
        this.statusService = statusService;
        this.tagService = tagService;
        this.attachmentService = attachmentService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(rollbackOn = {RuntimeException.class, Exception.class})
    public void createTask(CreateTaskDTO createTaskDTO, List<MultipartFile> attachments) throws ProjectNotFound, UserNotFound, StatusNotFound, TaskCreationLimitHasBeenReached, LimitReached, IOException {
       if(createTaskDTO.tags().size() > 3){
            throw new LimitReached("You can choose only maximum 3 tags for a task");
       }
        // LoggedIn User
      User task_creator = UserUtil.getCurrentUser();
      // task creator subscription
      Subscription task_creator_subscription = task_creator.getSubscription();
      // Get the project in which task is being created
      Project task_owner_project = projectService.findProjectWithProjectAssigneesAndTask(createTaskDTO.project_id());
      // Check the current plan
      if(UserUtil.isBasicPlan(task_creator_subscription)){
          if(task_owner_project.getProject_tasks().size() > task_creator_subscription.getPlan().getMax_tasks_per_project()){
             throw new TaskCreationLimitHasBeenReached("Your task creation limit has been reached. Please upgrade to premium package to create unlimited tasks");
          } else if (attachments.size() > task_creator.getSubscription().getPlan().getMax_attachment_per_task()) {
              throw new LimitReached("In your current basic plan, you can upload only upto 3 files for each task. Please upgrade to premium package to upload unlimited files to each task");
          }
      }
      Task task = modelMapper.map(createTaskDTO, Task.class);

      task.setTask_id(null);
      task.setCreatedBy(task_creator);
      task.setStartDate(createTaskDTO.startDate().atZone(ZoneOffset.UTC).toInstant());
      task.setDueDate(createTaskDTO.dueDate().atZone(ZoneOffset.UTC).toInstant());
      task.setProject(task_owner_project);
      task.setAssignee(userService.getUserByEmail(createTaskDTO.assignee_email()));
      task.setTask_status(statusService.getStatusByName(createTaskDTO.status()));
      task.setTask_tags(new HashSet<>(tagService.getTagsByName(createTaskDTO.tags())));
      // Task attachment
      List<Attachment> uploadedFiles = attachmentService.uploadTaskAttachmentsToCloud(attachments, task);
      task.setTask_attachments(uploadedFiles);
      task = taskRepository.save(task);

        Set<User> project_assignees = task_owner_project.getProject_assignees();
        if(!project_assignees.contains(task.getAssignee())){
            project_assignees.add(task.getAssignee());
            task_owner_project.setProject_assignees(project_assignees);
            projectService.updateProject(task_owner_project);
        }
        applicationEventPublisher.publishEvent(new TaskCreatedEvent(task));
    }

    public void changeTaskStatus(String taskId, ChangeStatusDTO changeStatusDTO) throws TaskNotFound, StatusNotFound, InvalidStatusSelected {
      Task task = taskRepository.findById(taskId)
              .orElseThrow(() -> new TaskNotFound("Task not found"));
      Status task_status = task.getTask_status();
      if(task_status.getStatus_name().equalsIgnoreCase(changeStatusDTO.newStatus())){
          throw new InvalidStatusSelected("New task status is still same");
      }
      // new task status
      task_status = statusService.getStatusByName(changeStatusDTO.newStatus());
      task.setTask_status(task_status);

      taskRepository.save(task);
    }
    public TaskDTO getTaskById(String task_id) throws TaskNotFound {
       Task task = taskRepository.getTaskById(task_id)
                .orElseThrow(() -> new TaskNotFound("Task not found"));
      TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
      taskDTO.setCreator(new TaskUserDTO(task.getCreatedBy().getName(), task.getCreatedBy().getEmail(),
      task.getCreatedBy().getProfile_pic()));
      taskDTO.setAssignee(new TaskUserDTO(task.getAssignee().getName(), task.getAssignee().getEmail(),
      task.getAssignee().getProfile_pic()));
      return taskDTO;
    }
    public void deleteTask(String task_id){
        taskRepository.deleteById(task_id);
    }
    public long findTaskAttachmentCount(String task_id){
        return attachmentService.findNumberOfAttachmentOfATaskById(task_id);
    }
    public List<Task> findDueReminderTasks(){
        return taskRepository.findDueReminderTask(Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES));
    }
    @Transactional
    public void changeTaskReminderStatus(String task_id){
        taskRepository.changeTaskReminderStatus(task_id);
    }
    @Transactional
    public void changeTaskStatusToOverDue(){
        taskRepository.changeTaskStatusToOverDue();
    }

}
