package com.tanno.taskmanager.service;

import com.tanno.taskmanager.dto.request.TaskRequest;
import com.tanno.taskmanager.dto.response.TaskResponse;
import com.tanno.taskmanager.dto.response.TaskSummaryResponse;
import com.tanno.taskmanager.enums.Priority;
import com.tanno.taskmanager.enums.TaskStatus;
import com.tanno.taskmanager.exception.BusinessException;
import com.tanno.taskmanager.exception.ResourceNotFoundException;
import com.tanno.taskmanager.model.Project;
import com.tanno.taskmanager.model.Task;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.ProjectRepository;
import com.tanno.taskmanager.repository.TaskRepository;
import com.tanno.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       UserRepository userRepository,
                       CurrentUserService currentUserService) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public TaskResponse create(Long projectId, TaskRequest request) {

        Project project = findProject(projectId);
        User assignee = findUser(request.getAssigneeId());

        boolean isMember = project.getMembers()
                .stream()
                .anyMatch(user ->
                        user.getId().equals(assignee.getId()));

        if (!isMember &&
                !project.getOwner().getId().equals(assignee.getId())) {

            throw new BusinessException(
                    "User does not belong to this project.");
        }

        User creator = currentUserService.getCurrentUser();

        Task task = buildTask(
                request,
                project,
                assignee,
                creator
        );

        return toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> findAllByProject(Long projectId) {

        Project project = findProject(projectId);

        checkProjectAccess(project);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public TaskResponse findById(Long id) {

        Task task = findTask(id);

        checkProjectAccess(task.getProject());

        return toResponse(task);
    }

    public List<TaskResponse> findAllFiltered(
            Long projectId,
            TaskStatus status,
            Priority priority,
            Long assigneeId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String sort
    ) {

        Project project = findProject(projectId);

        checkProjectAccess(project);


        List<Task> tasks =
                taskRepository.findByProjectId(projectId);



        return tasks.stream()

                .filter(task ->
                        status == null ||
                                task.getStatus() == status
                )

                .filter(task ->
                        priority == null ||
                                task.getPriority() == priority
                )

                .filter(task ->
                        assigneeId == null ||
                                task.getAssignee()
                                        .getId()
                                        .equals(assigneeId)
                )

                .filter(task ->
                        startDate == null ||
                                !task.getCreatedAt()
                                        .isBefore(startDate)
                )

                .filter(task ->
                        endDate == null ||
                                !task.getCreatedAt()
                                        .isAfter(endDate)
                )

                .sorted(getComparator(sort))

                .map(this::toResponse)

                .toList();
    }

    public TaskResponse update(Long id, TaskRequest request) {

        Task task = findTask(id);

        checkTaskManagementPermission(task);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());

        return toResponse(taskRepository.save(task));
    }

    public void delete(Long id) {

        Task task = findTask(id);

        checkTaskManagementPermission(task);

        taskRepository.delete(task);
    }

    public TaskResponse updateStatus(Long id, TaskStatus status) {

        Task task = findTask(id);

        checkStatusUpdatePermission(task);

        if (task.getPriority() == Priority.CRITICAL
                && status == TaskStatus.DONE) {

            checkCriticalTaskPermission(task);
        }

        if (task.getStatus() == TaskStatus.DONE &&
                status != TaskStatus.DONE) {

            throw new BusinessException(
                    "A completed task cannot revert to a previous status.");
        }

        if (status == TaskStatus.IN_PROGRESS &&
                task.getStatus() != TaskStatus.IN_PROGRESS) {

            long count = taskRepository.countByAssigneeIdAndStatus(
                    task.getAssignee().getId(),
                    TaskStatus.IN_PROGRESS
            );

            if (count >= 5) {
                throw new BusinessException(
                        "The user already has 5 tasks in progress.");
            }
        }

        validateStatusChange(
                task.getStatus(),
                status
        );

        task.setStatus(status);

        return toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> search(String text) {

        return taskRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        text,
                        text
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskSummaryResponse summary(Long projectId) {

        Project project = findProject(projectId);

        checkProjectAccess(project);

        List<Object[]> statusResults =
                taskRepository.countByStatus(projectId);

        List<Object[]> priorityResults =
                taskRepository.countByPriority(projectId);


        Map<TaskStatus, Long> byStatus =
                statusResults.stream()
                        .collect(Collectors.toMap(
                                x -> (TaskStatus)x[0],
                                x -> (Long)x[1]
                        ));


        Map<Priority, Long> byPriority =
                priorityResults.stream()
                        .collect(Collectors.toMap(
                                x -> (Priority)x[0],
                                x -> (Long)x[1]
                        ));


        return new TaskSummaryResponse(
                byStatus,
                byPriority
        );
    }

    private Project findProject(Long projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found."));
    }

    private Task findTask(Long taskId) {

        return taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found."));
    }

    private User findUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."));
    }

    private Task buildTask(
            TaskRequest request,
            Project project,
            User assignee,
            User creator) {

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());
        task.setProject(project);
        task.setAssignee(assignee);
        task.setCreatedBy(creator);
        task.setStatus(TaskStatus.TODO);

        return task;
    }

    private void checkProjectAccess(Project project) {

        User currentUser = currentUserService.getCurrentUser();

        boolean hasAccess =
                project.getOwner().getId().equals(currentUser.getId())
                        ||
                        project.getMembers()
                                .stream()
                                .anyMatch(user ->
                                        user.getId().equals(currentUser.getId()));

        if (!hasAccess) {
            throw new BusinessException(
                    "The user does not have access to the project.");
        }
    }

    private void checkTaskManagementPermission(Task task) {

        User currentUser = currentUserService.getCurrentUser();

        boolean canManage =
                task.getProject().getOwner().getId().equals(currentUser.getId())
                        ||
                        task.getCreatedBy().getId().equals(currentUser.getId());

        if (!canManage) {
            throw new BusinessException(
                    "The user does not have permission for this operation.");
        }
    }

    private void checkStatusUpdatePermission(Task task) {

        User currentUser = currentUserService.getCurrentUser();

        boolean canUpdate =
                task.getAssignee().getId().equals(currentUser.getId())
                        ||
                        task.getProject().getOwner().getId().equals(currentUser.getId());

        if (!canUpdate) {
            throw new BusinessException(
                    "The user cannot change the task status.");
        }
    }

    private void validateStatusChange(
            TaskStatus currentStatus,
            TaskStatus newStatus) {


        if (currentStatus == TaskStatus.TODO
                && newStatus == TaskStatus.DONE) {

            throw new BusinessException(
                    "A TODO task cannot be changed to DONE directly."
            );
        }

    }

    private void checkCriticalTaskPermission(Task task) {

        User currentUser = currentUserService.getCurrentUser();

        boolean isAdmin =
                task.getProject()
                        .getOwner()
                        .getId()
                        .equals(currentUser.getId());


        if (!isAdmin) {
            throw new BusinessException(
                    "Only project admin can change task status."
            );
        }

    }

    private Comparator<Task> getComparator(String sort) {

        if (sort == null) {
            return Comparator.comparing(
                    Task::getCreatedAt
            ).reversed();
        }

        return switch(sort) {

            case "priority" ->
                    Comparator.comparing(
                            Task::getPriority
                    );

            case "deadline" ->
                    Comparator.comparing(
                            Task::getDeadline,
                            Comparator.nullsLast(
                                    Comparator.naturalOrder()
                            )
                    );

            case "createdAt" ->
                    Comparator.comparing(
                            Task::getCreatedAt
                    );

            default ->
                    Comparator.comparing(
                            Task::getCreatedAt
                    ).reversed();

        };

    }

    private TaskResponse toResponse(Task task) {

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDeadline(),
                task.getProject().getId(),
                task.getAssignee().getId(),
                task.getCreatedBy().getId()
        );
    }
}