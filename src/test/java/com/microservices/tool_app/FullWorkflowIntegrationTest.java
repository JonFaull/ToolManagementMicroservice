package com.microservices.tool_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FullWorkflowIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullWorkflow_endToEnd_success() throws Exception {

        // 1. Create user
        UserDto user = new UserDto();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        // 2. Create tool
        ToolDto tool = new ToolDto();
        tool.setToolName("Hammer");
        tool.setToolType("Hand Tool");
        tool.setUserId(1L);

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tool)))
                .andExpect(status().isCreated());

        // 3. Update tool
        tool.setToolId(1L);
        tool.setToolName("Hammer XL");

        mockMvc.perform(put("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tool)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("Tool updated successfully"));

        // 4. Delete tool
        mockMvc.perform(delete("/api/tools/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("Tool deleted successfully"));

        // 5. Delete user
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("User deleted successfully"));
    }
}
