package com.tanno.taskmanager.service;

import com.tanno.taskmanager.dto.request.ProjectRequest;
import com.tanno.taskmanager.dto.response.ProjectResponse;
import com.tanno.taskmanager.exception.BusinessException;
import com.tanno.taskmanager.exception.ResourceNotFoundException;
import com.tanno.taskmanager.model.Project;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.ProjectRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void shouldCreateProjectSuccessfully() {

        User owner = new User();
        owner.setId(1L);

        ProjectRequest request = new ProjectRequest();
        request.setName("Projeto Teste");
        request.setDescription("Descrição");

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> {

                    Project project = invocation.getArgument(0);
                    project.setId(10L);

                    return project;
                });

        ProjectResponse response =
                projectService.create(request);

        assertNotNull(response);
        assertEquals("Projeto Teste", response.getName());
        assertEquals(1L, response.getOwnerId());

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void shouldFindProjectByIdSuccessfully() {

        User owner = new User();
        owner.setId(1L);

        Project project = new Project();
        project.setId(5L);
        project.setName("Projeto");
        project.setDescription("Descrição");
        project.setOwner(owner);

        when(projectRepository.findById(5L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);


        ProjectResponse response =
                projectService.findById(5L);

        assertEquals(5L, response.getId());
        assertEquals("Projeto", response.getName());

    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotHaveAccessToProject() {

        User owner = new User();
        owner.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);

        Project project = new Project();
        project.setId(5L);
        project.setName("Projeto");
        project.setOwner(owner);

        when(projectRepository.findById(5L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(anotherUser);

        assertThrows(
                BusinessException.class,
                () -> projectService.findById(5L)
        );

    }

    @Test
    void shouldThrowExceptionWhenProjectDoesNotExist() {

        when(projectRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.findById(99L)
        );
    }

    @Test
    void shouldDeleteProjectSuccessfully() {

        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        projectService.delete(1L);

        verify(projectRepository).delete(project);
    }

    @Test
    void shouldAddMemberToProjectSuccessfully() {

        User owner = new User();
        owner.setId(1L);

        User user = new User();
        user.setId(2L);
        user.setName("Douglas");

        Project project = new Project();
        project.setId(1L);
        project.setOwner(owner);
        project.setMembers(new HashSet<>());

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(owner);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ProjectResponse response = projectService.addMember(1L, 2L);

        assertTrue(project.getMembers().contains(user));

        verify(projectRepository)
                .save(project);

    }

    @Test
    void shouldNotAllowMemberToAddAnotherMember() {

        User owner = new User();
        owner.setId(1L);

        User member = new User();
        member.setId(2L);

        User newUser = new User();
        newUser.setId(3L);

        Project project = new Project();

        project.setId(1L);
        project.setOwner(owner);
        project.setMembers(new HashSet<>());

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        when(currentUserService.getCurrentUser())
                .thenReturn(member);

        assertThrows(BusinessException.class,
                () -> projectService.addMember(1L, 3L)
        );

    }

}
