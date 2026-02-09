package com.microservices.tool_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long userId;

    @NotEmpty(message = "Name can not be null or empty")
    private String name;

    @NotEmpty(message = "Email can not be null or empty")
    @Pattern(regexp = ".*@.*", message = "Value must contain '@'")
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth must not be null and in format: yyyy-mm-dd")
    private LocalDate dateOfBirth;
}
