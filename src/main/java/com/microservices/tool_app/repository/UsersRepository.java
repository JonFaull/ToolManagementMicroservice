package com.microservices.tool_app.repository;

import com.microservices.tool_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);
}
