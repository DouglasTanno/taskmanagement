package com.tanno.taskmanager.controller;

import com.tanno.taskmanager.dto.request.ProjectRequest;
import com.tanno.taskmanager.dto.response.ProjectResponse;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.UserRepository;
import com.tanno.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    public ProjectController(ProjectService projectService,
                             UserRepository userRepository) {
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(
            @Valid @RequestBody ProjectRequest request) {

        return projectService.create(request);
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(
            @PathVariable Long id) {

        return projectService.findById(id);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {

        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        projectService.delete(id);
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ProjectResponse addMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {

        return projectService.addMember(projectId, userId);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ProjectResponse removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {

        return projectService.removeMember(projectId, userId);
    }
}