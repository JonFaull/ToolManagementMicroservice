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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ToolControllerIntegrationTest extends BaseIntegrationTest {

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

    private ToolDto buildTool(Long userId) {
        ToolDto dto = new ToolDto();
        dto.setToolName("Hammer");
        dto.setToolType("Hand Tool");
        dto.setUserId(userId);
        return dto;
    }

    private Long extractIdFromLocation(MvcResult result) {
        String location = result.getResponse().getHeader("Location");
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    void createTool_success() throws Exception {

        // Create user
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = extractIdFromLocation(userResult);

        // Create tool
        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool(userId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg").value("Tool created successfully"));
    }

    @Test
    void createTool_userNotFound() throws Exception {
        ToolDto dto = buildTool(999L);

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

        // Create user
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = extractIdFromLocation(userResult);

        // Create tool
        MvcResult toolResult = mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool(userId))))
                .andExpect(status().isCreated())
                .andReturn();

        Long toolId = extractIdFromLocation(toolResult);

        // Get tool
        mockMvc.perform(get("/api/tools/" + toolId))
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

        // Create user
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = extractIdFromLocation(userResult);

        // Create tool
        MvcResult toolResult = mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool(userId))))
                .andExpect(status().isCreated())
                .andReturn();

        Long toolId = extractIdFromLocation(toolResult);

        // Update tool
        ToolDto updated = buildTool(userId);
        updated.setToolId(toolId);
        updated.setToolName("Hammer XL");

        mockMvc.perform(put("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("Tool updated successfully"));
    }

    @Test
    void updateTool_notFound() throws Exception {
        ToolDto dto = buildTool(1L);
        dto.setToolId(999L);

        mockMvc.perform(put("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTool_success() throws Exception {

        // Create user
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = extractIdFromLocation(userResult);

        // Create tool
        MvcResult toolResult = mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool(userId))))
                .andExpect(status().isCreated())
                .andReturn();

        Long toolId = extractIdFromLocation(toolResult);

        // Delete tool
        mockMvc.perform(delete("/api/tools/" + toolId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("Tool deleted successfully"));
    }

    @Test
    void deleteTool_notFound() throws Exception {
        mockMvc.perform(delete("/api/tools/999"))
                .andExpect(status().isNotFound());
    }
}
