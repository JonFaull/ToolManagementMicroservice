package com.microservices.tool_app.service_impl;

import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.entity.Tool;
import com.microservices.tool_app.entity.User;
import com.microservices.tool_app.mapper.ToolMapper;
import com.microservices.tool_app.repository.ToolsRepository;
import com.microservices.tool_app.repository.UsersRepository;
import com.microservices.tool_app.service.IToolService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ToolServiceImpl implements IToolService {

    private final ToolsRepository toolsRepository;
    private final UsersRepository usersRepository;

    // ✅ FIXED: return ToolDto instead of void
    @Override
    public ToolDto createTool(ToolDto dto) {

        Tool tool = new Tool();
        tool.setToolName(dto.getToolName());
        tool.setToolType(dto.getToolType());

        User user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        tool.setUser(user);
        tool.setCreatedAt(LocalDateTime.now());

        // ✅ Save and capture generated ID
        Tool savedTool = toolsRepository.save(tool);

        // ✅ Return DTO containing generated toolId
        return ToolMapper.mapToToolsDto(savedTool);
    }

    @Override
    public List<ToolDto> getAllTools() {
        return toolsRepository.findAll()
                .stream()
                .map(ToolMapper::mapToToolsDto)
                .toList();
    }

    @Override
    public Page<ToolDto> getAllTools(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<Tool> toolsPage = toolsRepository.findAll(pageable);

        return toolsPage.map(ToolMapper::mapToToolsDto);
    }

    @Override
    public ToolDto getToolById(Long id) {
        return toolsRepository.findById(id)
                .map(ToolMapper::mapToToolsDto)
                .orElse(null);
    }

    @Override
    public List<ToolDto> getToolsByUserId(Long id) {
        return toolsRepository.findByUser_UserId(id)
                .stream()
                .map(ToolMapper::mapToToolsDto)
                .toList();
    }

    @Override
    public boolean updateTool(ToolDto dto) {

        Optional<Tool> existingOpt = toolsRepository.findById(dto.getToolId());
        if (existingOpt.isEmpty()) {
            return false;
        }

        Tool tool = existingOpt.get();
        tool.setToolName(dto.getToolName());
        tool.setToolType(dto.getToolType());

        User user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        tool.setUser(user);

        toolsRepository.save(tool);
        return true;
    }

    @Override
    public boolean deleteTool(Long id) {
        if (!toolsRepository.existsById(id)) {
            return false;
        }

        toolsRepository.deleteById(id);
        return true;
    }
}
