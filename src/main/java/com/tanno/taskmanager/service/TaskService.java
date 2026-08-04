package com.tanno.taskmanager.service;

import com.tanno.taskmanager.dto.request.TaskRequest;
import com.tanno.taskmanager.dto.response.TaskResponse;
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

import java.util.List;

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
                    "Usuário não pertence ao projeto.");
        }

        User creator = currentUserService.getCurrentUser();

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());
        task.setProject(project);
        task.setAssignee(assignee);
        task.setCreatedBy(creator);
        task.setStatus(TaskStatus.TODO);

        return toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> findAllByProject(Long projectId) {

        Project project = findProject(projectId);

        validateProjectAccess(project);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {

        Task task = findTask(id);

        validateProjectAccess(task.getProject());

        return toResponse(task);
    }

    public TaskResponse update(Long id, TaskRequest request) {

        Task task = findTask(id);

        validateTaskManagement(task);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());

        return toResponse(taskRepository.save(task));
    }

    public void delete(Long id) {

        Task task = findTask(id);

        validateTaskManagement(task);

        taskRepository.delete(task);
    }

    public TaskResponse updateStatus(Long id, TaskStatus status) {

        Task task = findTask(id);

        validateStatusUpdatePermission(task);

        if (task.getStatus() == TaskStatus.DONE &&
                status != TaskStatus.DONE) {

            throw new BusinessException(
                    "Uma tarefa concluída não pode voltar de status.");
        }

        if (status == TaskStatus.IN_PROGRESS &&
                task.getStatus() != TaskStatus.IN_PROGRESS) {

            long count = taskRepository.countByAssigneeIdAndStatus(
                    task.getAssignee().getId(),
                    TaskStatus.IN_PROGRESS
            );

            if (count >= 5) {
                throw new BusinessException(
                        "O usuário já possui 5 tarefas em andamento.");
            }
        }

        task.setStatus(status);

        return toResponse(taskRepository.save(task));
    }

    private Project findProject(Long projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Projeto não encontrado"));
    }

    private Task findTask(Long taskId) {

        return taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tarefa não encontrada"));
    }

    private User findUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"));
    }

    private void validateProjectAccess(Project project) {

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
                    "Usuário não possui acesso ao projeto.");
        }
    }

    private void validateTaskManagement(Task task) {

        User currentUser = currentUserService.getCurrentUser();

        boolean canManage =
                task.getProject().getOwner().getId().equals(currentUser.getId())
                        ||
                        task.getCreatedBy().getId().equals(currentUser.getId());

        if (!canManage) {
            throw new BusinessException(
                    "Usuário não possui permissão para esta operação.");
        }
    }

    private void validateStatusUpdatePermission(Task task) {

        User currentUser = currentUserService.getCurrentUser();

        boolean canUpdate =
                task.getAssignee().getId().equals(currentUser.getId())
                        ||
                        task.getProject().getOwner().getId().equals(currentUser.getId());

        if (!canUpdate) {
            throw new BusinessException(
                    "Usuário não pode alterar o status da tarefa.");
        }
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