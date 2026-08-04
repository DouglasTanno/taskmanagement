package com.tanno.taskmanager.repository;

import com.tanno.taskmanager.enums.TaskStatus;
import com.tanno.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    long countByAssigneeIdAndStatus(
            Long assigneeId,
            TaskStatus status
    );
}