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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
        project.getMembers().add(owner);

        User assignee = new User();
        assignee.setId(2L);

        Task task = new Task();
        task.setProject(project);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.TODO);
        task.setCreatedBy(owner);

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
        project.getMembers().add(owner);

        Task task = new Task();
        task.setProject(project);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.TODO);
        task.setCreatedBy(owner);

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

        assertEquals("The user already has 5 tasks in progress.",exception.getMessage()
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldFilterTasksByStatus() {

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);
        project.setMembers(new HashSet<>());

        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task done");
        task1.setStatus(TaskStatus.DONE);
        task1.setProject(project);
        task1.setAssignee(owner);
        task1.setCreatedBy(owner);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task progress");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setProject(project);
        task2.setAssignee(owner);
        task2.setCreatedBy(owner);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(taskRepository.findByProjectId(1L))
                .thenReturn(List.of(task1, task2));

        List<TaskResponse> response =
                taskService.findAllFiltered(
                        1L,
                        TaskStatus.DONE,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertEquals(1, response.size());
        assertEquals(TaskStatus.DONE, response.get(0).getStatus());
    }

    @Test
    void shouldFilterTasksByPriority() {

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        Task high = new Task();
        high.setId(1L);
        high.setPriority(Priority.HIGH);
        high.setProject(project);
        high.setAssignee(owner);
        high.setCreatedBy(owner);

        Task low = new Task();
        low.setId(2L);
        low.setPriority(Priority.LOW);
        low.setProject(project);
        low.setAssignee(owner);
        low.setCreatedBy(owner);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(taskRepository.findByProjectId(1L))
                .thenReturn(List.of(high, low));

        List<TaskResponse> response =
                taskService.findAllFiltered(
                        1L,
                        null,
                        Priority.HIGH,
                        null,
                        null,
                        null,
                        null
                );

        assertEquals(1, response.size());
        assertEquals(
                Priority.HIGH,
                response.get(0).getPriority()
        );

    }

    @Test
    void shouldFilterTasksByAssignee() {

        User owner = new User();
        owner.setId(1L);

        User member = new User();
        member.setId(2L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        Task task = new Task();
        task.setId(1L);
        task.setProject(project);
        task.setAssignee(member);
        task.setCreatedBy(owner);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(taskRepository.findByProjectId(1L))
                .thenReturn(List.of(task));

        List<TaskResponse> response =
                taskService.findAllFiltered(
                        1L,
                        null,
                        null,
                        2L,
                        null,
                        null,
                        null
                );

        assertEquals(1, response.size());
        assertEquals(
                2L,
                response.get(0).getAssigneeId()
        );

    }

    @Test
    void shouldFilterTasksByDateRange() {

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        Task oldTask = new Task();
        oldTask.setId(1L);
        oldTask.setCreatedAt(LocalDateTime.of(2025,1,1,0,0));
        oldTask.setProject(project);
        oldTask.setAssignee(owner);
        oldTask.setCreatedBy(owner);

        Task newTask = new Task();
        newTask.setId(2L);
        newTask.setCreatedAt(LocalDateTime.of(2026,1,1,0,0));
        newTask.setProject(project);
        newTask.setAssignee(owner);
        newTask.setCreatedBy(owner);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(taskRepository.findByProjectId(1L))
                .thenReturn(List.of(oldTask,newTask));

        List<TaskResponse> response =
                taskService.findAllFiltered(
                        1L,
                        null,
                        null,
                        null,
                        LocalDateTime.of(2026,1,1,0,0),
                        null,
                        null
                );

        assertEquals(1,response.size());
        assertEquals(2L,response.get(0).getId());

    }

    @Test
    void shouldSortTasksByDeadline() {

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);

        Task later = new Task();
        later.setId(1L);
        later.setDeadline(LocalDateTime.of(2026,12,1,0,0));
        later.setProject(project);
        later.setAssignee(owner);
        later.setCreatedBy(owner);

        Task sooner = new Task();
        sooner.setId(2L);
        sooner.setDeadline(LocalDateTime.of(2026,5,1,0,0));
        sooner.setProject(project);
        sooner.setAssignee(owner);
        sooner.setCreatedBy(owner);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(taskRepository.findByProjectId(1L))
                .thenReturn(List.of(later, sooner));

        List<TaskResponse> response =
                taskService.findAllFiltered(
                        1L,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "deadline"
                );

        assertEquals(2L,response.get(0).getId());

    }

    @Test
    void shouldSearchTasksByTitleOrDescription() {

        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setOwner(user);

        Task task = new Task();

        task.setId(10L);
        task.setTitle("Implementar login");
        task.setDescription("Criar autenticação JWT");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.HIGH);
        task.setProject(project);
        task.setAssignee(user);
        task.setCreatedBy(user);

        when(taskRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "login",
                        "login"
                ))
                .thenReturn(List.of(task));

        List<TaskResponse> response = taskService.search("login");

        assertEquals(1,response.size());

        assertEquals("Implementar login",response.get(0).getTitle());

        assertEquals("Criar autenticação JWT",response.get(0).getDescription());

        verify(taskRepository)
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "login",
                        "login"
                );

    }

    @Test
    void shouldReturnEmptyListWhenSearchHasNoResults() {

        when(taskRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "naoexiste",
                        "naoexiste"
                ))
                .thenReturn(List.of());

        List<TaskResponse> response =
                taskService.search("naoexiste");

        assertTrue(response.isEmpty());

        verify(taskRepository)
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "naoexiste",
                        "naoexiste"
                );

    }
}
