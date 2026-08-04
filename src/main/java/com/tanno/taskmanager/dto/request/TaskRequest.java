package com.tanno.taskmanager.dto.request;

import com.tanno.taskmanager.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;

    private Priority priority;

    private LocalDateTime deadline;

    private Long assigneeId;
}