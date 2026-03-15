package com.project_management.project_management.service;

import com.project_management.project_management.Dtos.task.CreateTaskDTO;
import com.project_management.project_management.exception.project.ProjectNotFound;
import com.project_management.project_management.exception.task.StatusNotFound;
import com.project_management.project_management.exception.user.UserNotFound;
import com.project_management.project_management.model.Project;
import com.project_management.project_management.model.Task;
import com.project_management.project_management.model.User;
import com.project_management.project_management.repository.TaskRepository;
import com.project_management.project_management.util.UserUtil;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.HashSet;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final ProjectService projectService;
    private final AuthService authService;
    private final StatusService statusService;
    private final TagService tagService;

    public TaskService(final TaskRepository taskRepository, final ModelMapper modelMapper,
                       final ProjectService projectService, final AuthService authService,
                       final StatusService statusService, final TagService tagService){
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.projectService = projectService;
        this.authService = authService;
        this.statusService = statusService;
        this.tagService = tagService;
    }
    public void createTask(CreateTaskDTO createTaskDTO) throws ProjectNotFound, UserNotFound, StatusNotFound {
      // LoggedIn User
      User task_creator = UserUtil.getCurrentUser();
      Task task = modelMapper.map(createTaskDTO, Task.class);

      task.setTask_id(null);
      task.setCreatedBy(task_creator);
      task.setStartDate(createTaskDTO.startDate().atStartOfDay().toInstant(ZoneOffset.UTC));
      task.setDueDate(createTaskDTO.dueDate().atStartOfDay().toInstant(ZoneOffset.UTC));
      task.setProject(projectService.getProjectById(createTaskDTO.project_id()));
      task.setAssignee(authService.getUserByEmail(createTaskDTO.assignee_email()));
      task.setTask_status(statusService.getStatusByName(createTaskDTO.status()));
      task.setTask_tags(new HashSet<>(tagService.getTagsByName(createTaskDTO.tags())));

      taskRepository.save(task);
    }
}
