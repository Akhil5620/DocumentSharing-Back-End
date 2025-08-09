package com.documentshare.controller;

import com.documentshare.dto.UserDto;
import com.documentshare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin portal endpoints")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;


    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Get list of all users (Admin only)")
    public ResponseEntity<List<UserDto.UserResponse>> getAllUsers() {
        try {
            List<UserDto.UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/users")
    @Operation(summary = "Create user", description = "Create a new user (Admin only)")
    public ResponseEntity<UserDto.UserResponse> createUser(@Valid @RequestBody UserDto.CreateUserRequest createRequest) {
        try {
            UserDto.UserResponse user = userService.createUser(createRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update user details and roles (Admin only)")
    public ResponseEntity<UserDto.UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserDto.UpdateUserRequest updateRequest) {
        try {
            java.util.Optional<UserDto.UserResponse> user = userService.updateUser(id, updateRequest);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Permanently delete a user (Admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID (Admin only)")
    public ResponseEntity<UserDto.UserResponse> getUserById(@PathVariable String id) {
        try {
            java.util.Optional<UserDto.UserResponse> user = userService.getUserById(id);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/users/active")
    @Operation(summary = "Get active users", description = "Get list of all active users (Admin only)")
    public ResponseEntity<List<UserDto.UserResponse>> getActiveUsers() {
        try {
            List<UserDto.UserResponse> users = userService.getActiveUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 