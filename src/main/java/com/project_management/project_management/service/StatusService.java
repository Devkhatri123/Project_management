package com.project_management.project_management.service;

import com.project_management.project_management.exception.task.StatusNotFound;
import com.project_management.project_management.model.Status;
import com.project_management.project_management.repository.StatusRepository;
import org.springframework.stereotype.Service;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    public StatusService(final StatusRepository statusRepository){
        this.statusRepository = statusRepository;
    }

    public Status getStatusByName(String status_name) throws StatusNotFound {
        return statusRepository.getStatusByName(status_name)
                .orElseThrow(() -> new StatusNotFound("Select available status name"));
    }
}
