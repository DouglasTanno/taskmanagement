package com.tanno.taskmanager.service;

import com.tanno.taskmanager.dto.request.TaskRequest;
import com.tanno.taskmanager.dto.response.TaskResponse;
import com.tanno.taskmanager.enums.Priority;
import com.tanno.taskmanager.enums.TaskStatus;
import com.tanno.taskmanager.exception.BusinessException;
import com.tanno.taskmanager.model.Project;
import com.tanno.taskmanager.model.Task;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.ProjectRepository;
import com.tanno.taskmanager.repository.TaskRepository;
import com.tanno.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskSuccessfully() {

        Project project = new Project();
        project.setId(1L);

        User owner = new User();
        owner.setId(1L);

        project.setOwner(owner);

        User assignee = new User();
        assignee.setId(2L);

        project.getMembers().add(assignee);

        User creator = new User();
        creator.setId(3L);

        TaskRequest request = new TaskRequest();
        request.setTitle("Task");
        request.setDescription("Description");
        request.setPriority(Priority.HIGH);
        request.setAssigneeId(2L);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(assignee));

        when(currentUserService.getCurrentUser())
                .thenReturn(creator);

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> {

                    Task task = invocation.getArgument(0);

                    task.setId(10L);

                    return task;

                });

        TaskResponse response =
                taskService.create(1L, request);

        assertNotNull(response);
        assertEquals("Task", response.getTitle());
        assertEquals(TaskStatus.TODO, response.getStatus());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenAssigneeIsNotProjectMember() {

        Project project = new Project();
        project.setId(1L);

        User owner = new User();
        owner.setId(1L);

        project.setOwner(owner);

        project.setMembers(new HashSet<>());

        User assignee = new User();
        assignee.setId(2L);

        TaskRequest request = new TaskRequest();
        request.setAssigneeId(2L);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(assignee));

        assertThrows(
                BusinessException.class,
                () -> taskService.create(1L, request)
        );

        verify(taskRepository, never())
                .save(any());
    }

    @Test
    void shouldDeleteTaskSuccessfully() {

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setOwner(owner);

        User creator = new User();
        creator.setId(1L);

        Task task = new Task();
        task.setProject(project);
        task.setCreatedBy(creator);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        taskService.delete(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowExceptionWhenTodoChangesDirectlyToDone() {

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setOwner(owner);

        User assignee = new User();
        assignee.setId(2L);

        Task task = new Task();
        task.setProject(project);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        assertThrows(
                BusinessException.class,
                () -> taskService.updateStatus(
                        1L,
                        TaskStatus.DONE
                )
        );

        verify(taskRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasFiveTasksInProgress() {

        User owner = new User();
        owner.setId(1L);

        User assignee = new User();
        assignee.setId(2L);

        Project project = new Project();
        project.setOwner(owner);

        Task task = new Task();
        task.setProject(project);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(taskRepository.countByAssigneeIdAndStatus(
                2L,
                TaskStatus.IN_PROGRESS))
                .thenReturn(5L);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> taskService.updateStatus(
                        1L,
                        TaskStatus.IN_PROGRESS)
        );

        assertEquals(
                "The user already has 5 tasks in progress.",
                exception.getMessage()
        );

        verify(taskRepository, never()).save(any(Task.class));
    }
}
