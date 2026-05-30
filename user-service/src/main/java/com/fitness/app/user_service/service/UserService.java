package com.fitness.app.user_service.service;

import com.fitness.app.user_service.dto.UserRequest;
import com.fitness.app.user_service.dto.UserResponse;
import com.fitness.app.user_service.exceptions.UserAlreadyExistException;
import com.fitness.app.user_service.exceptions.UserNotFoundException;
import com.fitness.app.user_service.model.User;
import com.fitness.app.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserResponse getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty())
        {
            throw new UserNotFoundException("User with user id: "+ id+" not found");
        }
        return userToUserResponse(user.get());

    }
    private final UserResponse userToUserResponse(User user)
    {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Transactional //It tells Spring: "Execute everything in this method within a single database transaction."
    public UserResponse saveUser(UserRequest userRequest) {

        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new UserAlreadyExistException(
                    "Username already exists: " + userRequest.getUsername()
            );
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistException(
                    "Email already exists: " + userRequest.getEmail()
            );
        }

        User user = userRequestToUser(userRequest);

        User savedUser = userRepository.save(user);

        return userToUserResponse(savedUser);
    }

//    @Transactional
//    public UserResponse saveUser(UserRequest userRequest) {
//
//        if (userRepository.existsByUsername(userRequest.getUsername())) {
//            throw new UserAlreadyExistException("Username already exists");
//        }
//
//        if (userRepository.existsByEmail(userRequest.getEmail())) {
//            throw new UserAlreadyExistException("Email already exists");
//        }
//
//        User user = userRequestToUser(userRequest);
//
//        User savedUser = userRepository.save(user);
//
//        return userToUserResponse(savedUser);
//    }
    private User userRequestToUser(UserRequest userRequest)
    {
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .email(userRequest.getEmail())
                .userRole(userRequest.getUserRole())
                .build();
    }

    public UserResponse getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
        {
            throw new UserNotFoundException("User with email: "+ email+" not found");
        }
        return userToUserResponse(user.get());
    }
}
