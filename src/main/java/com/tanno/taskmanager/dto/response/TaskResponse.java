package com.tanno.taskmanager.dto.response;

import com.tanno.taskmanager.enums.Priority;
import com.tanno.taskmanager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private Priority priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deadline;

    private Long projectId;

    private Long assigneeId;

    private Long createdById;

    private Long projectOwnerId;
}