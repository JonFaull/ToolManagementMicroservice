package com.microservices.tool_app.controllers;

import com.microservices.tool_app.constants.ToolConstants;
import com.microservices.tool_app.dto.PaginatedResponseDto;
import com.microservices.tool_app.dto.ResponseDto;
import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.exceptions.ResourceNotFoundException;
import com.microservices.tool_app.service.IToolService;
import com.microservices.tool_app.service.IUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ToolController {

    private final IToolService toolService;
    private final IUserService userService;

    @PostMapping("/tools")
    public ResponseEntity<ResponseDto> createTool(@Valid @RequestBody ToolDto toolDto) {

        if (userService.getUserById(toolDto.getUserId()) == null) {
            throw new ResourceNotFoundException("User with ID " + toolDto.getUserId() + " does not exist");
        }

        toolService.createTool(toolDto);

        URI location = URI.create("/api/tools/" + toolDto.getToolId());

        return ResponseEntity
                .created(location)
                .body(new ResponseDto(
                        ToolConstants.STATUS_201,
                        ToolConstants.MESSAGE_201
                ));
    }

    @GetMapping("/tools")
    public ResponseEntity<List<ToolDto>> getAllTools() {
        return ResponseEntity.ok(toolService.getAllTools());
    }

    @GetMapping("/tools/paginated")
    public ResponseEntity<PaginatedResponseDto<ToolDto>> getAllToolsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "toolId") String sortBy) {

        Page<ToolDto> toolsPage = toolService.getAllTools(page, size, sortBy);

        PaginatedResponseDto<ToolDto> response = new PaginatedResponseDto<>(
                toolsPage.getContent(),
                toolsPage.getNumber(),
                toolsPage.getSize(),
                toolsPage.getTotalElements(),
                toolsPage.getTotalPages(),
                toolsPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}/tools")
    public ResponseEntity<List<ToolDto>> getToolsForUser(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(toolService.getToolsByUserId(id));
    }

    @GetMapping("/tools/{id}")
    public ResponseEntity<ToolDto> getToolById(@Valid @PathVariable Long id) {

        ToolDto tool = toolService.getToolById(id);

        if (tool == null) {
            throw new ResourceNotFoundException("Tool not found with ID: " + id);
        }

        return ResponseEntity.ok(tool);
    }

    @PutMapping("/tools")
    public ResponseEntity<ResponseDto> updateTool(@Valid @RequestBody ToolDto toolDto) {

        boolean isUpdated = toolService.updateTool(toolDto);

        if (!isUpdated) {
            throw new ResourceNotFoundException("Tool not found with ID: " + toolDto.getToolId());
        }

        return ResponseEntity.ok(
                new ResponseDto(
                        ToolConstants.STATUS_200,
                        ToolConstants.MESSAGE_200
                )
        );
    }

    @DeleteMapping("/tools/{id}")
    public ResponseEntity<ResponseDto> deleteTool(@Valid @PathVariable Long id) {

        boolean isDeleted = toolService.deleteTool(id);

        //Test CI pipelinettttttkkkt.

        if (!isDeleted) {
            throw new ResourceNotFoundException("Tool not found with ID: " + id);
        }

        return ResponseEntity.ok(
                new ResponseDto(
                        ToolConstants.STATUS_200,
                        ToolConstants.MESSAGE_200
                )
        );
    }
}
