package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public boolean adminRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public boolean isSelf(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Long id = (Long) authentication.getPrincipal();
        return userId.equals(id);
    }

}
