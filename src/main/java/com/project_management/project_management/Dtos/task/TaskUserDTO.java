package com.project_management.project_management.Dtos.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskUserDTO {
    private String name;
    private String email;
    private String profile_pic;

    public TaskUserDTO(){

    }
}
