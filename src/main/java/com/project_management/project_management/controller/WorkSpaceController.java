package com.project_management.project_management.controller;

import com.project_management.project_management.Dtos.CreateWorkSpaceDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspace")
public class WorkSpaceController {
    @PostMapping("/")
    public void createWorkSpace(@Valid @RequestBody CreateWorkSpaceDTO createWorkSpaceDTO,
                                @RequestHeader String userTimeZone){

    }
}
