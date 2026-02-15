package com.microservices.tool_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.tool_app.constants.BaseConstants;
import com.microservices.tool_app.constants.UserConstants;
import com.microservices.tool_app.dto.UserDto;
import com.microservices.tool_app.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto buildValidUser() {
        UserDto user = new UserDto();
        user.setUserId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void createUser_returns201() throws Exception {
        UserDto inputDto = buildValidUser();
        inputDto.setUserId(null);
        UserDto savedDto = buildValidUser();

        when(userService.createUser(any(UserDto.class))).thenReturn(savedDto);

        String json = objectMapper.writeValueAsString(inputDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.statusCode").value(BaseConstants.STATUS_201))
                .andExpect(jsonPath("$.statusMsg").value(UserConstants.MESSAGE_201));
    }

    @Test
    void getUsers_returnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(buildValidUser()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(buildValidUser());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void getUserById_throws404_whenNotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void getUserByEmail_returnsUser() throws Exception {
        when(userService.getUserByEmail("john@example.com")).thenReturn(buildValidUser());

        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserByEmail_throws404_whenNotFound() throws Exception {
        when(userService.getUserByEmail("missing@example.com")).thenReturn(null);

        mockMvc.perform(get("/api/users/email/missing@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void getUsersByDOBRange_returnsList() throws Exception {
        when(userService.getUsersByDOBRange(any(), any()))
                .thenReturn(List.of(buildValidUser()));

        mockMvc.perform(get("/api/users/dob-range")
                        .param("startDate", "1980-01-01")
                        .param("endDate", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    void getUsersByDOBRange_throws400_whenStartAfterEnd() throws Exception {
        mockMvc.perform(get("/api/users/dob-range")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2020-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void getUsersByDOBRange_throws400_whenFutureDates() throws Exception {
        mockMvc.perform(get("/api/users/dob-range")
                        .param("startDate", "2050-01-01")
                        .param("endDate", "2051-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void updateUserDetails_returns200_whenUpdated() throws Exception {
        UserDto dto = buildValidUser();

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(true);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(BaseConstants.STATUS_200))
                .andExpect(jsonPath("$.statusMsg").value(UserConstants.MESSAGE_200_UPDATE));
    }

    @Test
    void updateUserDetails_throws404_whenNotFound() throws Exception {
        UserDto dto = buildValidUser();

        when(userService.updateUser(eq(99L), any(UserDto.class))).thenReturn(false);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void deleteUser_returns200_whenDeleted() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(BaseConstants.STATUS_200))
                .andExpect(jsonPath("$.statusMsg").value(UserConstants.MESSAGE_200_DELETE));
    }

    @Test
    void deleteUser_throws404_whenNotFound() throws Exception {
        when(userService.deleteUser(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }
}
