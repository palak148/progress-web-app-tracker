package com.preptracker.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final CustomUserDetailsService userDetailsService;

    public SecurityUtils(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public com.preptracker.entity.User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userDetailsService.loadUserEntityByEmail(email);
        }
        throw new IllegalStateException("No authenticated user");
    }
}
