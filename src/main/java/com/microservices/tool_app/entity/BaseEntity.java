package com.microservices.tool_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter @ToString
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
