package com.microservices.tool_app.mapper;

import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.entity.Tool;
import com.microservices.tool_app.entity.User;

public class ToolMapper {

    public static ToolDto mapToToolsDto(Tool tool) {
        ToolDto dto = new ToolDto();
        dto.setToolId(tool.getToolId());
        dto.setToolName(tool.getToolName());
        dto.setToolType(tool.getToolType());
        if (tool.getUser() != null) {
            dto.setUserId(tool.getUser().getUserId());
        }

        return dto;
    }

    public static Tool mapToTools(ToolDto dto, Tool tool, User user) {
        tool.setToolName(dto.getToolName());
        tool.setToolType(dto.getToolType());
        tool.setUser(user);

        return tool;
    }
}
