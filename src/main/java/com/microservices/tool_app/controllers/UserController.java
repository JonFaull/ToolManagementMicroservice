package com.microservices.tool_app.controllers;

import com.microservices.tool_app.constants.BaseConstants;
import com.microservices.tool_app.constants.UserConstants;
import com.microservices.tool_app.dto.ResponseDto;
import com.microservices.tool_app.dto.UserDto;
import com.microservices.tool_app.exceptions.ResourceNotFoundException;
import com.microservices.tool_app.service.IUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final IUserService iUsersService;

    @PostMapping("/users")
    public ResponseEntity<ResponseDto> createUser(@Valid @RequestBody UserDto userDto) {

        iUsersService.createUser(userDto);

        URI location = URI.create("/api/users/" + userDto.getUserId());

        return ResponseEntity
                .created(location)
                .body(new ResponseDto(
                        BaseConstants.STATUS_201,
                        UserConstants.MESSAGE_201
                ));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(iUsersService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@Valid @PathVariable Long id) {

        UserDto user = iUsersService.getUserById(id);

        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@Valid @PathVariable String email) {

        UserDto user = iUsersService.getUserByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/dob-range")
    public ResponseEntity<List<UserDto>> getUsersByDOBRange(
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both startDate and endDate are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Dates cannot be in the future");
        }

        return ResponseEntity.ok(iUsersService.getUsersByDOBRange(startDate, endDate));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ResponseDto> updateUserDetails(
            @Valid @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {

        boolean isUpdated = iUsersService.updateUser(id, userDto);

        if (!isUpdated) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        return ResponseEntity.ok(
                new ResponseDto(
                        BaseConstants.STATUS_200,
                        UserConstants.MESSAGE_417_UPDATE   // or MESSAGE_200 if you prefer
                )
        );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ResponseDto> deleteUser(@Valid @PathVariable Long id) {

        boolean isDeleted = iUsersService.deleteUser(id);

        if (!isDeleted) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        return ResponseEntity.ok(
                new ResponseDto(
                        BaseConstants.STATUS_200,
                        UserConstants.MESSAGE_417_DELETE   // or "User deleted successfully"
                )
        );
    }

}
