package com.microservices.tool_app.service_impl;

import com.microservices.tool_app.dto.ToolDto;
import com.microservices.tool_app.entity.Tool;
import com.microservices.tool_app.entity.User;
import com.microservices.tool_app.repository.ToolsRepository;
import com.microservices.tool_app.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolServiceImplTest {

    @Mock
    private ToolsRepository toolsRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private ToolServiceImpl toolService;

    private ToolDto toolDto;
    private User user;
    private Tool tool;

    @BeforeEach
    void setup() {
        toolDto = new ToolDto();
        toolDto.setToolId(1L);
        toolDto.setToolName("Hammer");
        toolDto.setToolType("Hand Tool");
        toolDto.setUserId(10L);

        user = new User();
        user.setUserId(10L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        tool = new Tool();
        tool.setToolId(1L);
        tool.setToolName("Hammer");
        tool.setToolType("Hand Tool");
        tool.setUser(user);
    }

    // ---------------------------------------------------------
    // CREATE TOOL
    // ---------------------------------------------------------
    @Test
    void createTool_savesToolSuccessfully() {
        when(usersRepository.findById(10L)).thenReturn(Optional.of(user));

        when(toolsRepository.save(any(Tool.class))).thenReturn(tool);

        toolService.createTool(toolDto);

        ArgumentCaptor<Tool> captor = ArgumentCaptor.forClass(Tool.class);
        verify(toolsRepository).save(captor.capture());

        Tool saved = captor.getValue();
        assertThat(saved.getToolName()).isEqualTo("Hammer");
        assertThat(saved.getToolType()).isEqualTo("Hand Tool");
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getCreatedAt()).isNotNull();
    }


    @Test
    void createTool_throwsWhenUserNotFound() {
        when(usersRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> toolService.createTool(toolDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ---------------------------------------------------------
    // GET ALL TOOLS
    // ---------------------------------------------------------
    @Test
    void getAllTools_returnsMappedDtos() {
        when(toolsRepository.findAll()).thenReturn(List.of(tool));

        List<ToolDto> result = toolService.getAllTools();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToolName()).isEqualTo("Hammer");
    }

    // ---------------------------------------------------------
    // GET PAGINATED TOOLS
    // ---------------------------------------------------------
    @Test
    void getAllToolsPaginated_returnsMappedPage() {
        Page<Tool> page = new PageImpl<>(List.of(tool));
        when(toolsRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ToolDto> result = toolService.getAllTools(0, 10, "toolId");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getToolName()).isEqualTo("Hammer");
    }

    // ---------------------------------------------------------
    // GET TOOL BY ID
    // ---------------------------------------------------------
    @Test
    void getToolById_returnsDtoWhenFound() {
        when(toolsRepository.findById(1L)).thenReturn(Optional.of(tool));

        ToolDto result = toolService.getToolById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getToolName()).isEqualTo("Hammer");
    }

    @Test
    void getToolById_returnsNullWhenNotFound() {
        when(toolsRepository.findById(1L)).thenReturn(Optional.empty());

        ToolDto result = toolService.getToolById(1L);

        assertThat(result).isNull();
    }

    // ---------------------------------------------------------
    // GET TOOLS BY USER ID
    // ---------------------------------------------------------
    @Test
    void getToolsByUserId_returnsMappedList() {
        when(toolsRepository.findByUser_UserId(10L)).thenReturn(List.of(tool));

        List<ToolDto> result = toolService.getToolsByUserId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToolName()).isEqualTo("Hammer");
    }

    // ---------------------------------------------------------
    // UPDATE TOOL
    // ---------------------------------------------------------
    @Test
    void updateTool_returnsTrueWhenUpdated() {
        when(toolsRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(usersRepository.findById(10L)).thenReturn(Optional.of(user));

        boolean result = toolService.updateTool(toolDto);

        assertThat(result).isTrue();
        verify(toolsRepository).save(any(Tool.class));
    }

    @Test
    void updateTool_returnsFalseWhenToolNotFound() {
        when(toolsRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = toolService.updateTool(toolDto);

        assertThat(result).isFalse();
        verify(toolsRepository, never()).save(any());
    }

    @Test
    void updateTool_throwsWhenUserNotFound() {
        when(toolsRepository.findById(1L)).thenReturn(Optional.of(tool));
        when(usersRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> toolService.updateTool(toolDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ---------------------------------------------------------
    // DELETE TOOL
    // ---------------------------------------------------------
    @Test
    void deleteTool_returnsTrueWhenDeleted() {
        when(toolsRepository.existsById(1L)).thenReturn(true);

        boolean result = toolService.deleteTool(1L);

        assertThat(result).isTrue();
        verify(toolsRepository).deleteById(1L);
    }

    @Test
    void deleteTool_returnsFalseWhenNotFound() {
        when(toolsRepository.existsById(1L)).thenReturn(false);

        boolean result = toolService.deleteTool(1L);

        assertThat(result).isFalse();
        verify(toolsRepository, never()).deleteById(any());
    }
}
