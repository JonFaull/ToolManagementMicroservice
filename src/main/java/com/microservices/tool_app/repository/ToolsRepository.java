package com.microservices.tool_app.repository;

import com.microservices.tool_app.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolsRepository extends JpaRepository<Tool, Long> {

    Optional<Tool> findByToolName(String toolName);
    List<Tool> findByUser_UserId(Long userId);
}
