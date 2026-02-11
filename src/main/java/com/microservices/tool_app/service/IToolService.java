package com.microservices.tool_app.service;

import com.microservices.tool_app.dto.ToolDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IToolService {

    ToolDto createTool(ToolDto toolDto);

    List<ToolDto> getAllTools();
    Page<ToolDto> getAllTools(int page, int size, String sortBy);

    ToolDto getToolById(Long id);

    boolean updateTool(ToolDto toolDto);

    boolean deleteTool(Long id);

    List<ToolDto> getToolsByUserId(Long userId);

}
