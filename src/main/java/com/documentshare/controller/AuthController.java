package com.documentshare.controller;

import com.documentshare.dto.UserDto;
import com.documentshare.entity.User;
import com.documentshare.exception.ErrorResponse;
import com.documentshare.security.JwtTokenProvider;
import com.documentshare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account (USER role only)")
    public ResponseEntity<UserDto.UserResponse> registerUser(@Valid @RequestBody UserDto.CreateUserRequest createRequest) {
        try {
            UserDto.UserResponse user = userService.createUserPublic(createRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw e; // Let the global exception handler handle it
        }
    }


    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and get JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto.LoginRequest loginRequest) {
        try {
            // Find user by username or email
            java.util.Optional<User> userOpt = userService.getUserEntityByUsernameOrEmail(loginRequest.getUsernameOrEmail());
            
            if (userOpt.isEmpty()) {
                System.err.println("User not found: " + loginRequest.getUsernameOrEmail());
                ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(java.time.LocalDateTime.now())
                    .status(400)
                    .error("Authentication Failed")
                    .message("Invalid username or email: " + loginRequest.getUsernameOrEmail())
                    .path("/api/auth/login")
                    .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            User user = userOpt.get();
            System.err.println("Found user: " + user.getUsername());
            
            // Verify password
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            System.err.println("Password matches: " + passwordMatches);
            
            if (!passwordMatches) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(java.time.LocalDateTime.now())
                    .status(400)
                    .error("Authentication Failed")
                    .message("Invalid password for user: " + user.getUsername())
                    .path("/api/auth/login")
                    .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Check if user is active
            if (!user.isActive()) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(java.time.LocalDateTime.now())
                    .status(400)
                    .error("Account Disabled")
                    .message("User account is disabled: " + user.getUsername())
                    .path("/api/auth/login")
                    .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user);
            System.err.println("Generated token: " + token.substring(0, Math.min(50, token.length())));
            
            // Create response
            UserDto.LoginResponse response = new UserDto.LoginResponse();
            response.setToken(token);
            response.setType("Bearer");
            
            // Convert user to response DTO
            UserDto.UserResponse userResponse = new UserDto.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
            );
            response.setUser(userResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(500)
                .error("Internal Server Error")
                .message("An error occurred during login: " + e.getMessage())
                .path("/api/auth/login")
                .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

} 