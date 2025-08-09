package com.documentshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class ValidRolesValidator implements ConstraintValidator<ValidRoles, Set<String>> {
    
    private static final Set<String> VALID_ROLES = new HashSet<>(Arrays.asList("USER", "ADMIN"));
    
    @Override
    public void initialize(ValidRoles constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(Set<String> roles, ConstraintValidatorContext context) {
        // If roles is null or empty, it's valid (will default to USER)
        if (roles == null || roles.isEmpty()) {
            return true;
        }
        
        // Check if all provided roles are valid
        for (String role : roles) {
            if (role == null || role.trim().isEmpty()) {
                return false;
            }
            if (!VALID_ROLES.contains(role.toUpperCase())) {
                return false;
            }
        }
        
        return true;
    }
} 