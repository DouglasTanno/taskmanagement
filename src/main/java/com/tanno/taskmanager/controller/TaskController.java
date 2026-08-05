package com.tanno.taskmanager.controller;

import com.tanno.taskmanager.dto.request.TaskRequest;
import com.tanno.taskmanager.dto.request.TaskStatusRequest;
import com.tanno.taskmanager.dto.response.TaskResponse;
import com.tanno.taskmanager.dto.response.TaskSummaryResponse;
import com.tanno.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks")
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

    @GetMapping("/search")
    public List<TaskResponse> search(
            @RequestParam String text) {

        return taskService.search(text);
    }

    @GetMapping("/summary")
    public TaskSummaryResponse summary(
            @PathVariable Long projectId) {

        return taskService.summary(projectId);
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {

        return taskService.update(taskId, request);
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

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long taskId) {

        taskService.delete(taskId);
    }
}