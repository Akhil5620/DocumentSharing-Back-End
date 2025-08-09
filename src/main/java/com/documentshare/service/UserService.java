package com.documentshare.service;

import com.documentshare.dto.UserDto;
import com.documentshare.entity.User;
import com.documentshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    public UserDto.UserResponse createUserPublic(UserDto.CreateUserRequest createRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(createRequest.getUsername())) {
            throw new RuntimeException("Username already exists: " + createRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(createRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + createRequest.getEmail());
        }

        // Create new user entity
        User user = new User();
        user.setUsername(createRequest.getUsername());
        user.setEmail(createRequest.getEmail());
        user.setPassword(passwordEncoder.encode(createRequest.getPassword()));
        user.setFirstName(createRequest.getFirstName());
        user.setLastName(createRequest.getLastName());
        user.setActive(true);

        // Public registration can ONLY create USER roles
        user.addRole("USER");

        // Save user
        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    
    public UserDto.UserResponse createUser(UserDto.CreateUserRequest createRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(createRequest.getUsername())) {
            throw new RuntimeException("Username already exists: " + createRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(createRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + createRequest.getEmail());
        }

        // Create new user entity
        User user = new User();
        user.setUsername(createRequest.getUsername());
        user.setEmail(createRequest.getEmail());
        user.setPassword(passwordEncoder.encode(createRequest.getPassword()));
        user.setFirstName(createRequest.getFirstName());
        user.setLastName(createRequest.getLastName());
        user.setActive(true);

        // Admin can create any role
        if (createRequest.getRoles() == null || createRequest.getRoles().isEmpty()) {
            user.addRole("USER");
        } else {
            user.setRoles(createRequest.getRoles());
        }

        // Save user
        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    
    @Transactional(readOnly = true)
    public Optional<UserDto.UserResponse> getUserById(String id) {
        return userRepository.findById(id)
                .map(this::convertToUserResponse);
    }

    
    @Transactional(readOnly = true)
    public Optional<UserDto.UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToUserResponse);
    }

    
    @Transactional(readOnly = true)
    public List<UserDto.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public List<UserDto.UserResponse> getActiveUsers() {
        return userRepository.findByActiveTrue().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    
    public Optional<UserDto.UserResponse> updateUser(String id, UserDto.UpdateUserRequest updateRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    // Update fields if provided
                    if (updateRequest.getUsername() != null) {
                        if (!updateRequest.getUsername().equals(user.getUsername()) &&
                                userRepository.existsByUsername(updateRequest.getUsername())) {
                            throw new RuntimeException("Username already exists: " + updateRequest.getUsername());
                        }
                        user.setUsername(updateRequest.getUsername());
                    }

                    if (updateRequest.getEmail() != null) {
                        if (!updateRequest.getEmail().equals(user.getEmail()) &&
                                userRepository.existsByEmail(updateRequest.getEmail())) {
                            throw new RuntimeException("Email already exists: " + updateRequest.getEmail());
                        }
                        user.setEmail(updateRequest.getEmail());
                    }

                    if (updateRequest.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
                    }

                    if (updateRequest.getFirstName() != null) {
                        user.setFirstName(updateRequest.getFirstName());
                    }

                    if (updateRequest.getLastName() != null) {
                        user.setLastName(updateRequest.getLastName());
                    }

                    if (updateRequest.getRoles() != null) {
                        user.setRoles(updateRequest.getRoles());
                    }

                    if (updateRequest.getActive() != null) {
                        user.setActive(updateRequest.getActive());
                    }

                    user.setUpdatedAt(LocalDateTime.now());
                    User savedUser = userRepository.save(user);
                    return convertToUserResponse(savedUser);
                });
    }

    
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    
    @Transactional(readOnly = true)
    public Optional<User> getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    
    @Transactional(readOnly = true)
    public Optional<User> getUserEntityByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    
    private UserDto.UserResponse convertToUserResponse(User user) {
        return new UserDto.UserResponse(
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
    }
} 