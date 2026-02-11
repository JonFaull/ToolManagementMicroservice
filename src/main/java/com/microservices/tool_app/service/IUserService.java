package com.microservices.tool_app.service;

import com.microservices.tool_app.dto.UserDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IUserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> getAllUsers();
    Page<UserDto> getAllUsers(int page, int size, String sortBy);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getUsersByDOBRange(LocalDate startDate, LocalDate endDate);
    boolean updateUser(Long id, UserDto userDto);
    boolean deleteUser(Long id);
}
