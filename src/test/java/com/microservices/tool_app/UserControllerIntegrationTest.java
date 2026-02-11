package com.microservices.tool_app;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class UserControllerIntegrationTest extends BaseIntegrationTest{

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

    @Test
    void createUser_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMsg").value("User created successfully"));
    }

    @Test
    void getUsers_success() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserById_notFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getUserByEmail_notFound() throws Exception {
        mockMvc.perform(get("/api/users/email/missing@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersByDOBRange_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users/dob-range")
                        .param("startDate", "1980-01-01")
                        .param("endDate", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void getUsersByDOBRange_invalidDates() throws Exception {
        mockMvc.perform(get("/api/users/dob-range")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2020-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        UserDto updated = buildUser();
        updated.setName("Updated Name");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("User updated successfully"));
    }

    @Test
    void updateUser_notFound() throws Exception {
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_success() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMsg").value("User deleted successfully"));
    }

    @Test
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
