package com.tanno.taskmanager.dto.response;

import com.tanno.taskmanager.enums.Priority;
import com.tanno.taskmanager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class TaskSummaryResponse {

    private Map<TaskStatus, Long> byStatus;

    private Map<Priority, Long> byPriority;
}