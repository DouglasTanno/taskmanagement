package com.tanno.taskmanager.service;

import com.tanno.taskmanager.dto.request.ProjectRequest;
import com.tanno.taskmanager.dto.response.ProjectResponse;
import com.tanno.taskmanager.dto.response.UserResponse;
import com.tanno.taskmanager.exception.BusinessException;
import com.tanno.taskmanager.exception.ResourceNotFoundException;
import com.tanno.taskmanager.model.Project;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.ProjectRepository;
import com.tanno.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, CurrentUserService currentUserService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public ProjectResponse create(ProjectRequest request) {

        User owner = currentUserService.getCurrentUser();

        Project project = new Project();

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(owner);

        Project saved = projectRepository.save(project);

        return toResponse(saved);
    }

    public List<ProjectResponse> findAll() {

        User currentUser =
                currentUserService.getCurrentUser();

        return projectRepository
                .findByOwnerIdOrMembersId(
                        currentUser.getId(),
                        currentUser.getId()
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse findById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Projeto não encontrado"
                        ));

        checkProjectAccess(project);

        return toResponse(project);
    }

    public ProjectResponse update(Long id, ProjectRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Projeto não encontrado"
                        ));

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project saved = projectRepository.save(project);

        return toResponse(saved);
    }

    public void delete(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Projeto não encontrado"
                        ));

        projectRepository.delete(project);
    }

    private void checkProjectManagementPermission(Project project) {

        User currentUser =
                currentUserService.getCurrentUser();


        boolean isOwner =
                project.getOwner()
                        .getId()
                        .equals(currentUser.getId());


        if (!isOwner) {

            throw new BusinessException(
                    "Only the project owner can manage members."
            );

        }

    }

    private ProjectResponse toResponse(Project project) {

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getMembers()
                        .stream()
                        .map(User::getId)
                        .collect(Collectors.toSet())
        );
    }

    public List<UserResponse> findMembers(Long projectId) {

        Project project = findProject(projectId);
        List<User> users = new ArrayList<>();

        users.add(project.getOwner());
        users.addAll(project.getMembers());


        return users.stream()
                .distinct()
                .map(this::toUserResponse)
                .toList();
    }

    public ProjectResponse addMember(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Projeto não encontrado"
                        ));

        checkProjectManagementPermission(project);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado"
                        ));

        project.getMembers().add(user);

        Project saved = projectRepository.save(project);

        return toResponse(saved);
    }

    public ProjectResponse removeMember(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Projeto não encontrado"
                        ));

        checkProjectManagementPermission(project);

        project.getMembers()
                .removeIf(user -> user.getId().equals(userId));

        Project saved = projectRepository.save(project);

        return toResponse(saved);
    }

    private Project findProject(Long projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Projeto não encontrado"
                        ));
    }

    private UserResponse toUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

    }

    private void checkProjectAccess(Project project) {

        User currentUser =
                currentUserService.getCurrentUser();

        boolean hasAccess =
                project.getOwner().getId()
                        .equals(currentUser.getId())
                        ||
                        project.getMembers()
                                .stream()
                                .anyMatch(user ->
                                        user.getId()
                                                .equals(currentUser.getId())
                                );

        if (!hasAccess) {

            throw new BusinessException(
                    "The user does not have access to this project."
            );

        }
    }
}