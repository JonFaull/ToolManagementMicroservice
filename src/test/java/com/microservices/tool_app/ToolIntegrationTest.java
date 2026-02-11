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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ToolIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTool_endToEnd_success() throws Exception {

        UserDto user = new UserDto();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        ToolDto tool = new ToolDto();
        tool.setToolName("Hammer");
        tool.setToolType("Hand Tool");
        tool.setUserId(1L); // H2 auto-increments starting at 1

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tool)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg").value("Tool created successfully"));
    }
}
