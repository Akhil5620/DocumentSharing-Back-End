package com.documentshare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import com.documentshare.validation.ValidRoles;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


public class UserDto {

    // Request DTOs
    @Schema(description = "Request DTO for creating a new user")
    public static class CreateUserRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Unique username for the user", example = "john_doe")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
                message = "Please provide a valid email address")
        @Schema(description = "User's email address", example = "john.doe@example.com")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$", 
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
        @Schema(description = "User's password (must contain uppercase, lowercase, number, and special character)", example = "SecurePass123!")
        private String password;

        @NotBlank(message = "First name is required")
        @Schema(description = "User's first name", example = "John")
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Schema(description = "User's last name", example = "Doe")
        private String lastName;

        @Size(max = 10, message = "Maximum 10 roles allowed")
        @ValidRoles
        @Schema(description = "User roles (USER or ADMIN)", example = "[\"USER\"]")
        private Set<String> roles = new HashSet<>();

        // Constructors
        public CreateUserRequest() {}

        public CreateUserRequest(String username, String email, String password, String firstName, String lastName) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }
    }

    @Schema(description = "Request DTO for updating an existing user")
    public static class UpdateUserRequest {
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Unique username for the user", example = "john_doe_updated")
        private String username;

        @Email(message = "Email should be valid")
        @Schema(description = "User's email address", example = "john.doe.updated@example.com")
        private String email;

        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        @Schema(description = "User's password (must contain uppercase, lowercase, number, and special character)", example = "NewSecurePass123!")
        private String password;

        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Schema(description = "User's first name", example = "John")
        private String firstName;
        
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Schema(description = "User's last name", example = "Doe")
        private String lastName;
        
        @Size(max = 10, message = "Maximum 10 roles allowed")
        @ValidRoles
        @Schema(description = "User roles (USER or ADMIN)", example = "[\"ADMIN\"]")
        private Set<String> roles;
        
        @Schema(description = "User account status", example = "true")
        private Boolean active;

        // Constructors
        public UpdateUserRequest() {}

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    // Response DTOs
    @Schema(description = "Response DTO for user information")
    public static class UserResponse {
        @Schema(description = "Unique user ID", example = "507f1f77bcf86cd799439011")
        private String id;

        @Schema(description = "User's username", example = "john_doe")
        private String username;

        @Schema(description = "User's email address", example = "john.doe@example.com")
        private String email;

        @Schema(description = "User's first name", example = "John")
        private String firstName;

        @Schema(description = "User's last name", example = "Doe")
        private String lastName;

        @Schema(description = "User roles", example = "[\"USER\"]")
        private Set<String> roles;

        @Schema(description = "User account status", example = "true")
        private boolean active;

        @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
        private LocalDateTime createdAt;

        @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
        private LocalDateTime updatedAt;

        public UserResponse(String id, String username, String email, String firstName, String lastName,
                          Set<String> roles, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
            this.active = active;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    @Schema(description = "Request DTO for user login")
    public static class LoginRequest {
        @NotBlank(message = "Username or email is required")
        @Schema(description = "Username or email address", example = "john_doe")
        private String usernameOrEmail;

        @NotBlank(message = "Password is required")
        @Schema(description = "User's password", example = "SecurePass123!")
        private String password;

        // Constructors
        public LoginRequest() {}

        public LoginRequest(String usernameOrEmail, String password) {
            this.usernameOrEmail = usernameOrEmail;
            this.password = password;
        }

        // Getters and Setters
        public String getUsernameOrEmail() {
            return usernameOrEmail;
        }

        public void setUsernameOrEmail(String usernameOrEmail) {
            this.usernameOrEmail = usernameOrEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Schema(description = "Response DTO for login success")
    public static class LoginResponse {
        @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        private String token;

        @Schema(description = "Token type", example = "Bearer")
        private String type = "Bearer";

        @Schema(description = "User information")
        private UserResponse user;

        // Constructors
        public LoginResponse() {}

        public LoginResponse(String token, UserResponse user) {
            this.token = token;
            this.user = user;
        }

        // Getters and Setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public UserResponse getUser() {
            return user;
        }

        public void setUser(UserResponse user) {
            this.user = user;
        }
    }
} 