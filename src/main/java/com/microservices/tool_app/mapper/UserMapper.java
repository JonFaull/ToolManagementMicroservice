package com.microservices.tool_app.mapper;

import com.microservices.tool_app.dto.UserDto;
import com.microservices.tool_app.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static UserDto mapToUsersDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDateOfBirth(user.getDateOfBirth());
        return dto;
    }

    public static User mapToUsers(UserDto dto, User user) {
        user.setUserId(dto.getUserId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setDateOfBirth(dto.getDateOfBirth());
        return user;
    }
}
