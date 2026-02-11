package com.microservices.tool_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.tool_app.constants.ToolConstants;
import com.microservices.tool_app.dto.PaginatedResponseDto;
import com.microservices.tool_app.dto.ResponseDto;
import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.dto.UserDto;
import com.microservices.tool_app.exceptions.ResourceNotFoundException;
import com.microservices.tool_app.service.IToolService;
import com.microservices.tool_app.service.IUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToolController.class)
class ToolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IToolService toolService;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    // CREATE TOOL
    // ---------------------------------------------------------
    @Test
    void createTool_returns201_whenUserExists() throws Exception {
        ToolDto dto = new ToolDto();
        dto.setToolId(1L);
        dto.setToolName("Hammer");
        dto.setToolType("Hand Tool");
        dto.setUserId(10L);

        // Build a valid UserDto
        UserDto user = new UserDto();
        user.setUserId(10L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(userService.getUserById(10L)).thenReturn(user);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tools/1"))
                .andExpect(jsonPath("$.status").value(ToolConstants.STATUS_201))
                .andExpect(jsonPath("$.message").value(ToolConstants.MESSAGE_201));
    }


    @Test
    void createTool_throws404_whenUserDoesNotExist() throws Exception {
        ToolDto dto = new ToolDto();
        dto.setToolId(1L);
        dto.setUserId(99L);

        when(userService.getUserById(99L)).thenReturn(null);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // GET ALL TOOLS
    // ---------------------------------------------------------
    @Test
    void getAllTools_returnsList() throws Exception {
        ToolDto tool = new ToolDto();
        tool.setToolId(1L);
        tool.setToolName("Saw");

        when(toolService.getAllTools()).thenReturn(List.of(tool));

        mockMvc.perform(get("/api/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toolId").value(1L))
                .andExpect(jsonPath("$[0].toolName").value("Saw"));
    }

    // ---------------------------------------------------------
    // GET PAGINATED TOOLS
    // ---------------------------------------------------------
    @Test
    void getAllToolsPaginated_returnsPaginatedResponse() throws Exception {
        ToolDto tool = new ToolDto();
        tool.setToolId(1L);
        tool.setToolName("Drill");

        Page<ToolDto> page = new PageImpl<>(List.of(tool));

        when(toolService.getAllTools(anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/tools/paginated?page=0&size=10&sortBy=toolId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].toolId").value(1L))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    // ---------------------------------------------------------
    // GET TOOLS FOR USER
    // ---------------------------------------------------------
    @Test
    void getToolsForUser_returnsList() throws Exception {
        ToolDto tool = new ToolDto();
        tool.setToolId(1L);

        when(toolService.getToolsByUserId(5L)).thenReturn(List.of(tool));

        mockMvc.perform(get("/api/users/5/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toolId").value(1L));
    }

    // ---------------------------------------------------------
    // GET TOOL BY ID
    // ---------------------------------------------------------
    @Test
    void getToolById_returnsTool() throws Exception {
        ToolDto tool = new ToolDto();
        tool.setToolId(1L);

        when(toolService.getToolById(1L)).thenReturn(tool);

        mockMvc.perform(get("/api/tools/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolId").value(1L));
    }

    @Test
    void getToolById_throws404_whenNotFound() throws Exception {
        when(toolService.getToolById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/tools/99"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // UPDATE TOOL
    // ---------------------------------------------------------
    @Test
    void updateTool_returns200_whenUpdated() throws Exception {
        ToolDto dto = new ToolDto();
        dto.setToolId(1L);

        when(toolService.updateTool(dto)).thenReturn(true);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ToolConstants.STATUS_200))
                .andExpect(jsonPath("$.message").value(ToolConstants.MESSAGE_200));
    }

    @Test
    void updateTool_throws404_whenNotFound() throws Exception {
        ToolDto dto = new ToolDto();
        dto.setToolId(99L);

        when(toolService.updateTool(dto)).thenReturn(false);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // DELETE TOOL
    // ---------------------------------------------------------
    @Test
    void deleteTool_returns200_whenDeleted() throws Exception {
        when(toolService.deleteTool(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/tools/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ToolConstants.STATUS_200))
                .andExpect(jsonPath("$.message").value(ToolConstants.MESSAGE_200));
    }

    @Test
    void deleteTool_throws404_whenNotFound() throws Exception {
        when(toolService.deleteTool(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/tools/99"))
                .andExpect(status().isNotFound());
    }
}
