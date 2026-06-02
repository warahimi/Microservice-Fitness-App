package com.fitness.app.user_service.controller;

import com.fitness.app.user_service.dto.UserRequest;
import com.fitness.app.user_service.dto.UserResponse;
import com.fitness.app.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers()
    {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id)
    {
        System.out.println("id: "+id);
        System.out.println("Wahidullah");
        System.out.println("Rahimii");
        System.out.println("userService: "+userService);
        System.out.println("Hello");
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email)
    {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserRequest userRequest)
    {
        return new ResponseEntity<>(userService.saveUser(userRequest), HttpStatus.CREATED);
    }

    // validate the user by userId
    @GetMapping("/validate/{userId}")
    public ResponseEntity<Boolean> validateUserById(@PathVariable String userId)
    {
        return ResponseEntity.ok(userService.validateUserById(userId));
    }
}
