package com.documentshare.repository;

import com.documentshare.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    
    Optional<User> findByUsername(String username);

    
    Optional<User> findByEmail(String email);

    
    boolean existsByUsername(String username);

    
    boolean existsByEmail(String email);

    
    List<User> findByActiveTrue();

    
    @Query("{ 'roles': ?0 }")
    List<User> findByRole(String role);

    
    @Query("{ '$or': [ { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } } ] }")
    List<User> findByFirstNameContainingOrLastNameContainingIgnoreCase(String name);

    
    @Query(value = "{ 'roles': ?0 }", count = true)
    long countByRole(String role);

    
    List<User> findByCreatedAtAfter(java.time.LocalDateTime date);

    
    @Query("{ '$or': [ { 'username': ?0 }, { 'email': ?0 } ] }")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    
    List<User> findAllByOrderByCreatedAtDesc();

    
    List<User> findByActiveTrueOrderByCreatedAtDesc();
} 