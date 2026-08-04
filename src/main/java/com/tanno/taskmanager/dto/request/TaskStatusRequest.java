package com.tanno.taskmanager.dto.request;

import com.tanno.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusRequest {

    @NotNull
    private TaskStatus status;
}