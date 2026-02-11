package com.microservices.tool_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ToolIntegrationTest extends BaseIntegrationTest {

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
        if (location == null || location.isBlank()) {
            throw new IllegalStateException("Location header missing");
        }
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    void createTool_endToEnd_success() throws Exception {
        Long userId = extractIdFromLocation(mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated())
                .andReturn());

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTool(userId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg").value("Tool created successfully"));
    }
}
