package com.microservices.tool_app.service_impl;

import com.microservices.tool_app.dto.UserDto;
import com.microservices.tool_app.entity.User;
import com.microservices.tool_app.mapper.UserMapper;
import com.microservices.tool_app.repository.UsersRepository;
import com.microservices.tool_app.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private UsersRepository usersRepository;

    @Override
    public void createUser(UserDto userDto){
        User user = UserMapper.mapToUsers(userDto, new User());
        user.setCreatedAt(LocalDateTime.now() );
        usersRepository.save(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(UserMapper::mapToUsersDto)
                .toList();
    }

    @Override
    public Page<UserDto> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<User> usersPage = usersRepository.findAll(pageable);

        return usersPage.map(UserMapper::mapToUsersDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        return usersRepository.findById(id)
                .map(UserMapper::mapToUsersDto)
                .orElse(null);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .map(UserMapper::mapToUsersDto)
                .orElse(null);
    }

    public List<UserDto> getUsersByDOBRange(LocalDate startDate, LocalDate endDate) {
        return usersRepository.findByDateOfBirthBetween(startDate, endDate)
                .stream()
                .map(UserMapper::mapToUsersDto)
                .toList();
    }

    @Override
    public boolean updateUser(Long id, UserDto userDto) {
        Optional<User> existingUserOpt = usersRepository.findById(id);

        if (existingUserOpt.isEmpty()) {
            return false;
        }

        User existingUser = existingUserOpt.get();

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setDateOfBirth(userDto.getDateOfBirth());

        usersRepository.save(existingUser);
        return true;
    }

    @Override
    public boolean deleteUser(Long id) {

        if (!usersRepository.existsById(id)) {
            return false;
        }

        usersRepository.deleteById(id);

        return true;
    }

    public User getUserEntity(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

}
