package com.tanno.taskmanager.service;

import com.tanno.taskmanager.dto.request.ProjectRequest;
import com.tanno.taskmanager.dto.response.ProjectResponse;
import com.tanno.taskmanager.exception.ResourceNotFoundException;
import com.tanno.taskmanager.model.Project;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        ProjectResponse response =
                projectService.findById(5L);

        assertEquals(5L, response.getId());
        assertEquals("Projeto", response.getName());
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


}
