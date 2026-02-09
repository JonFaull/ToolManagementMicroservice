package com.microservices.tool_app.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ToolDto {
    private Long toolId;

    @NotEmpty(message = "Tool name cannot be null or empty")
    private String toolName;

    @NotEmpty(message = "Tool type cannot be null or empty")
    private String toolType;

    @NotNull(message = "User ID cannot be null")
    private Long userId;
}
