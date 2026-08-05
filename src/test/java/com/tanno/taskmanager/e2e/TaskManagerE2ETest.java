package com.tanno.taskmanager.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanno.taskmanager.model.Project;
import com.tanno.taskmanager.model.User;
import com.tanno.taskmanager.repository.ProjectRepository;
import com.tanno.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskManagerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateProjectAndTaskFlow() throws Exception {

        String email =
                "douglas" + System.currentTimeMillis() + "@test.com";

        // Criar usuário

        MvcResult userResult =
                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "name":"Douglas",
                            "email":"%s",
                            "password":"123456"
                        }
                        """.formatted(email)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name")
                                .value("Douglas"))
                        .andReturn();

        Long userId = extrairId(userResult);

        // Login

        MvcResult loginResult =
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "email":"%s",
                            "password":"123456"
                        }
                        """.formatted(email)))
                        .andExpect(status().isOk())
                        .andReturn();

        String token =
                extrairToken(loginResult);

        User user = userRepository.findById(userId).orElseThrow();

        Project project = new Project();
        project.setName("Projeto E2E");
        project.setDescription("Projeto de teste");
        project.setOwner(user);
        project.getMembers().add(user);

        project = projectRepository.save(project);

        Long projectId = project.getId();

        // Criar tarefa em projeto existente

        MvcResult taskResult =
                mockMvc.perform(post(
                                "/projects/" +
                                        projectId +
                                        "/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "title":"Minha tarefa",
                            "description":"Teste E2E",
                            "priority":"HIGH",
                            "assigneeId": %d
                        }
                        """.formatted(userId)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.title")
                                .value("Minha tarefa"))
                        .andReturn();

        Long taskId =
                extrairId(taskResult);

        // Altera tarefa de A FAZER para EM PROGRESSO

        mockMvc.perform(patch(
                        "/projects/" +
                                projectId +
                                "/tasks/" +
                                taskId +
                                "/status")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "status":"IN_PROGRESS"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("IN_PROGRESS"));

        // Altera tarefa de EM PROGRESSO para CONCLUIDA

        mockMvc.perform(patch(
                        "/projects/" +
                                projectId +
                                "/tasks/" +
                                taskId +
                                "/status")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "status":"DONE"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("DONE"));

        // Verificar tarefa concluída

        mockMvc.perform(get(
                        "/projects/" +
                                projectId +
                                "/tasks/" +
                                taskId)
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("DONE"));

    }

    private String extrairToken(MvcResult result)
            throws Exception {

        String response =
                result.getResponse()
                        .getContentAsString();

        ObjectMapper mapper =
                new ObjectMapper();

        JsonNode json =
                mapper.readTree(response);

        return json.get("token")
                .asText();

    }

    private Long extrairId(MvcResult result)
            throws Exception {

        String response =
                result.getResponse()
                        .getContentAsString();

        ObjectMapper mapper =
                new ObjectMapper();

        JsonNode json =
                mapper.readTree(response);

        return json.get("id")
                .asLong();

    }

}