package com.Shopping.Shopping.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService implements UserDetailsService {

    private static final String ADMIN_USERNAME = "AdisheshaR";
    // BCrypt hash for password: ADI@28RSCA
    private static final String ADMIN_PASSWORD = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!ADMIN_USERNAME.equals(username)) {
            throw new UsernameNotFoundException("Admin not found: " + username);
        }

        return User.builder()
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .roles("ADMIN")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
