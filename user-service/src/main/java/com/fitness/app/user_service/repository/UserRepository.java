package com.fitness.app.user_service.repository;

import com.fitness.app.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username); // less expensive than findByUsername and checking for null
    boolean existsByEmail(String email); // less expensive than findByEmail and checking for null
}
