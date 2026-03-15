package com.project_management.project_management.service;

import com.project_management.project_management.Dtos.project.CreateProjectDTO;
import com.project_management.project_management.enums.Plan_Enums.plan;
import com.project_management.project_management.exception.project.MaximumProjectCreationLimitReached;
import com.project_management.project_management.exception.project.ProjectNotFound;
import com.project_management.project_management.exception.workspace.WorkSpaceNotFound;
import com.project_management.project_management.model.*;
import com.project_management.project_management.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ProjectService {
    private final WorkSpaceService workSpaceService;
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(final WorkSpaceService workSpaceService, final ModelMapper modelMapper,
                          final ProjectRepository projectRepository){
        this.workSpaceService = workSpaceService;
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
    }
    public void createProjectInWorkSpace(CreateProjectDTO createProjectDTO) throws WorkSpaceNotFound, MaximumProjectCreationLimitReached {
     WorkSpace workSpace = workSpaceService.getWorkSpaceWithProjectByWorkSpaceKey(createProjectDTO.work_space_key());
     User workspaceOwner = workSpace.getOwner();
     if(workspaceOwner.getSubscription().getPlan().getPlanName() == plan.BASIC){
         if(workSpace.getMy_projects().size() >= workspaceOwner.getSubscription().getPlan().getMax_projects_per_workspace()){
            throw new MaximumProjectCreationLimitReached("Your project creation limit has been reached. Please upgrade to premium package to create unlimited projects");
         }
     }
    Project project = modelMapper.map(createProjectDTO, Project.class);
    project.setCreatedOn(Instant.now());
    project.setWorkSpace(workSpace);

    projectRepository.save(project);
    }

    public void updateProjectProgress(String project_id) throws ProjectNotFound {
      Project project = projectRepository.findProjectWithTask(project_id)
              .orElseThrow(() -> new ProjectNotFound("Project not found"));

     List<Task> project_Tasks = project.getProject_tasks();
     int totalTasks = project_Tasks.size();
     int completedTasks = 0;
     int project_new_progress_percentage = 0;

     if(totalTasks != 0) {
         for (Task task : project_Tasks) {
             Status task_status = task.getTask_status();
             if (task_status.getStatus_name().equals("Completed")) {
                 completedTasks++;
             }
         }
         project_new_progress_percentage = (completedTasks / totalTasks) * 100;
     }
     project.setProgress_percentage(project_new_progress_percentage);
     projectRepository.save(project);
    }
    public Project getProjectById(String project_id) throws ProjectNotFound {
      return projectRepository.findOnlyProjectById(project_id)
                .orElseThrow(() -> new ProjectNotFound("Project not found"));
    }
}
