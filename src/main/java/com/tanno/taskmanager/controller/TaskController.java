package com.tanno.taskmanager.controller;

import com.tanno.taskmanager.dto.request.TaskRequest;
import com.tanno.taskmanager.dto.request.TaskStatusRequest;
import com.tanno.taskmanager.dto.response.TaskResponse;
import com.tanno.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request) {

        return taskService.create(projectId, request);
    }

    @GetMapping
    public List<TaskResponse> findAll(
            @PathVariable Long projectId) {

        return taskService.findAllByProject(projectId);
    }

    @GetMapping("/{taskId}")
    public TaskResponse findById(
            @PathVariable Long taskId) {

        return taskService.findById(taskId);
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {

        return taskService.update(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long taskId) {

        taskService.delete(taskId);
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponse updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusRequest request) {

        return taskService.updateStatus(
                taskId,
                request.getStatus()
        );
    }

}