package com.microservices.tool_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.dto.UserDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ToolControllerIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto buildUser() {
        UserDto dto = new UserDto();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return dto;
    }

    private ToolDto buildTool() {
        ToolDto dto = new ToolDto();
        dto.setToolName("Hammer");
        dto.setToolType("Hand Tool");
        dto.setUserId(1L);
        return dto;
    }

    @Test
    void createTool_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg").value("Tool created successfully"));
    }

    @Test
    void createTool_userNotFound() throws Exception {
        ToolDto dto = buildTool();
        dto.setUserId(999L);

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTools_success() throws Exception {
        mockMvc.perform(get("/api/tools"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllToolsPaginated_success() throws Exception {
        mockMvc.perform(get("/api/tools/paginated?page=0&size=10&sortBy=toolId"))
                .andExpect(status().isOk());
    }

    @Test
    void getToolById_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/tools/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolName").value("Hammer"));
    }

    @Test
    void getToolById_notFound() throws Exception {
        mockMvc.perform(get("/api/tools/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTool_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool())))
                .andExpect(status().isCreated());

        ToolDto updated = buildTool();
        updated.setToolId(1L);
        updated.setToolName("Hammer XL");

        mockMvc.perform(put("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("Tool updated successfully"));
    }

    @Test
    void updateTool_notFound() throws Exception {
        ToolDto dto = buildTool();
        dto.setToolId(999L);

        mockMvc.perform(put("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTool_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool())))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/tools/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("Tool deleted successfully"));
    }

    @Test
    void deleteTool_notFound() throws Exception {
        mockMvc.perform(delete("/api/tools/999"))
                .andExpect(status().isNotFound());
    }
}
