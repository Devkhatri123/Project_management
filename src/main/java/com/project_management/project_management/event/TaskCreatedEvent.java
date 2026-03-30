package com.project_management.project_management.event;

import com.project_management.project_management.model.Task;

public class TaskCreatedEvent {
    private final Task createdTask;
    public TaskCreatedEvent(final Task createdTask){
        this.createdTask = createdTask;
    }
    public Task getCreatedTask(){
        return createdTask;
    }
}
