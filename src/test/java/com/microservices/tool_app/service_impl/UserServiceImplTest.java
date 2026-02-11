package com.microservices.tool_app.service_impl;

import com.microservices.tool_app.dto.UserDto;
import com.microservices.tool_app.entity.User;
import com.microservices.tool_app.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        userDto.setDateOfBirth(LocalDate.of(1990, 1, 1));

        user = new User();
        user.setUserId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    // ---------------------------------------------------------
    // CREATE USER
    // ---------------------------------------------------------
    @Test
    void createUser_savesUserSuccessfully() {
        when(usersRepository.save(any(User.class))).thenReturn(user);

        userService.createUser(userDto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
        assertThat(saved.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    // ---------------------------------------------------------
    // GET ALL USERS
    // ---------------------------------------------------------
    @Test
    void getAllUsers_returnsMappedDtos() {
        when(usersRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }

    // ---------------------------------------------------------
    // GET PAGINATED USERS
    // ---------------------------------------------------------
    @Test
    void getAllUsersPaginated_returnsMappedPage() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(usersRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<UserDto> result = userService.getAllUsers(0, 10, "userId");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("john@example.com");
    }

    // ---------------------------------------------------------
    // GET USER BY ID
    // ---------------------------------------------------------
    @Test
    void getUserById_returnsDtoWhenFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    void getUserById_returnsNullWhenNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.getUserById(1L);

        assertThat(result).isNull();
    }

    // ---------------------------------------------------------
    // GET USER BY EMAIL
    // ---------------------------------------------------------
    @Test
    void getUserByEmail_returnsDtoWhenFound() {
        when(usersRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByEmail("john@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getUserByEmail_returnsNullWhenNotFound() {
        when(usersRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        UserDto result = userService.getUserByEmail("missing@example.com");

        assertThat(result).isNull();
    }

    // ---------------------------------------------------------
    // GET USERS BY DOB RANGE
    // ---------------------------------------------------------
    @Test
    void getUsersByDOBRange_returnsMappedList() {
        when(usersRepository.findByDateOfBirthBetween(any(), any()))
                .thenReturn(List.of(user));

        List<UserDto> result = userService.getUsersByDOBRange(
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2000, 1, 1)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }

    // ---------------------------------------------------------
    // UPDATE USER
    // ---------------------------------------------------------
    @Test
    void updateUser_returnsTrueWhenUpdated() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.updateUser(1L, userDto);

        assertThat(result).isTrue();
        verify(usersRepository).save(any(User.class));
    }

    @Test
    void updateUser_returnsFalseWhenNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = userService.updateUser(1L, userDto);

        assertThat(result).isFalse();
        verify(usersRepository, never()).save(any());
    }

    // ---------------------------------------------------------
    // DELETE USER
    // ---------------------------------------------------------
    @Test
    void deleteUser_returnsTrueWhenDeleted() {
        when(usersRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(usersRepository).deleteById(1L);
    }

    @Test
    void deleteUser_returnsFalseWhenNotFound() {
        when(usersRepository.existsById(1L)).thenReturn(false);

        boolean result = userService.deleteUser(1L);

        assertThat(result).isFalse();
        verify(usersRepository, never()).deleteById(any());
    }

    // ---------------------------------------------------------
    // GET USER ENTITY (internal helper)
    // ---------------------------------------------------------
    @Test
    void getUserEntity_returnsEntityWhenFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserEntity(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getUserEntity_returnsNullWhenNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userService.getUserEntity(1L);

        assertThat(result).isNull();
    }
}
