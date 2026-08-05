package com.tanno.taskmanager.repository;

import com.tanno.taskmanager.enums.TaskStatus;
import com.tanno.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    long countByAssigneeIdAndStatus(
            Long assigneeId,
            TaskStatus status
    );

    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description
    );

    @Query("""
    SELECT t.status, COUNT(t)
    FROM Task t
    WHERE t.project.id = :projectId
    GROUP BY t.status
""")
    List<Object[]> countByStatus(Long projectId);


    @Query("""
    SELECT t.priority, COUNT(t)
    FROM Task t
    WHERE t.project.id = :projectId
    GROUP BY t.priority
""")
    List<Object[]> countByPriority(Long projectId);
}