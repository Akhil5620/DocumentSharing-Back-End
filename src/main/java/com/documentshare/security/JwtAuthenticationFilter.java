package com.documentshare.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                String userId = jwtTokenProvider.getUserIdFromToken(jwt);
                Set<String> roles = jwtTokenProvider.getRolesFromToken(jwt);

                System.err.println("JWT Authentication - Username: " + username);
                System.err.println("JWT Authentication - User ID: " + userId);
                System.err.println("JWT Authentication - Roles: " + roles);

                // Create authorities from roles
                var authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet());

                System.err.println("JWT Authentication - Authorities: " + authorities);

                // Create authentication token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                
                // Add user ID to authentication details
                authentication.setDetails(new UserDetails(userId, username, roles));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                System.err.println("JWT Authentication - Authentication set successfully");
            } else {
                System.err.println("JWT Authentication - Token validation failed");
            }
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
            System.err.println("JWT Authentication Error: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    
    public static class UserDetails {
        private final String userId;
        private final String username;
        private final Set<String> roles;

        public UserDetails(String userId, String username, Set<String> roles) {
            this.userId = userId;
            this.username = username;
            this.roles = roles;
        }

        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public Set<String> getRoles() { return roles; }
    }
} 